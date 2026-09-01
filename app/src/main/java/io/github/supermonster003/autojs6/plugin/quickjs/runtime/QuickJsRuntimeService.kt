package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import android.app.Application
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.quickjs.runtime.api.IQuickJsRuntimeCallback
import org.autojs.plugin.quickjs.runtime.api.IQuickJsRuntimePlugin
import org.autojs.plugin.quickjs.runtime.api.QuickJsRuntimeContract
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class QuickJsRuntimeService : Service() {
    private val executionGate = Semaphore(1, true)
    private val activeExecutions = ConcurrentHashMap<String, ExecutionHandle>()
    private val pendingCancellations = ConcurrentHashMap<String, Long>()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        activeExecutions.values.forEach(ExecutionHandle::cancel)
        activeExecutions.clear()
        pendingCancellations.clear()
        super.onDestroy()
    }

    private val binder = object : IQuickJsRuntimePlugin.Stub() {
        override fun getInfo(): PluginInfo {
            enforcePluginCaller()
            return applicationContext.quickJsPluginInfo()
        }

        override fun getRuntimeInfo(): Bundle {
            enforcePluginCaller()
            return runtimeInfo()
        }

        override fun prewarmRuntime(): Bundle {
            enforcePluginCaller()
            return runtimeInfo()
        }

        override fun cancelScript(executionId: String?): Boolean {
            enforcePluginCaller()
            if (executionId == null || !QuickJsExecutionRequestParser.isValidExecutionId(executionId)) {
                return false
            }
            activeExecutions[executionId]?.let { return it.cancel() }
            rememberPendingCancellation(executionId)
            return true
        }

        override fun runScript(
            requestBundle: Bundle?,
            source: ParcelFileDescriptor?,
            callback: IQuickJsRuntimeCallback?,
        ): Bundle {
            enforcePluginCaller()
            val startedAt = SystemClock.elapsedRealtime()
            var request: QuickJsExecutionRequest? = null
            var handle: ExecutionHandle? = null
            var gateAcquired = false
            try {
                request = QuickJsExecutionRequestParser.parse(requestBundle)
                requireNotNull(source) { "Source descriptor is missing" }
                requireNotNull(callback) { "Runtime callback is missing" }
                if (consumePendingCancellation(request.executionId)) {
                    return failureWithCallback(
                        callback,
                        request.executionId,
                        QuickJsRuntimeContract.ERROR_CANCELLED,
                        "Execution was cancelled",
                        startedAt,
                        cancelled = true,
                    )
                }
                if (!executionGate.tryAcquire()) {
                    return failureWithCallback(
                        callback,
                        request.executionId,
                        QuickJsRuntimeContract.ERROR_BUSY,
                        "QuickJS runtime is busy",
                        startedAt,
                    )
                }
                gateAcquired = true
                handle = ExecutionHandle()
                require(activeExecutions.putIfAbsent(request.executionId, handle) == null) {
                    "Execution ID is already active"
                }
                if (consumePendingCancellation(request.executionId)) handle.cancel()

                val sourceBytes = readSource(source)
                try {
                    if (handle.isCancelled()) {
                        return failureWithCallback(
                            callback,
                            request.executionId,
                            QuickJsRuntimeContract.ERROR_CANCELLED,
                            "Execution was cancelled",
                            startedAt,
                            cancelled = true,
                        )
                    }
                    val nativeHandle = NativeQuickJsRuntime.nativeCreateExecution(
                        request.timeoutMillis,
                        request.memoryLimitBytes,
                        request.stackLimitBytes,
                        request.outputByteLimit,
                    )
                    check(nativeHandle != 0L) { "Unable to allocate a native QuickJS execution" }
                    handle.attach(nativeHandle)
                    val output = QuickJsOutputSink(callback, request.executionId)
                    output.started()
                    val nativeResult = NativeQuickJsRuntime.nativeExecute(
                        nativeHandle,
                        sourceBytes,
                        request.sourceName,
                        if (request.sourceMode == QuickJsRuntimeContract.SOURCE_MODE_MODULE) 1 else 0,
                        output,
                    )
                    val terminal = terminalResult(request.executionId, nativeResult, startedAt)
                    output.finished(terminal)
                    return terminal
                } finally {
                    sourceBytes.fill(0)
                }
            } catch (error: SourceTooLargeException) {
                return failureWithCallback(
                    callback,
                    request?.executionId.orEmpty(),
                    QuickJsRuntimeContract.ERROR_SOURCE_TOO_LARGE,
                    error.message.orEmpty(),
                    startedAt,
                )
            } catch (error: IllegalArgumentException) {
                return failureWithCallback(
                    callback,
                    request?.executionId.orEmpty(),
                    QuickJsRuntimeContract.ERROR_INVALID_REQUEST,
                    error.message ?: "Invalid QuickJS request",
                    startedAt,
                )
            } catch (error: LinkageError) {
                return failureWithCallback(
                    callback,
                    request?.executionId.orEmpty(),
                    QuickJsRuntimeContract.ERROR_RUNTIME_UNAVAILABLE,
                    "QuickJS native runtime is unavailable",
                    startedAt,
                )
            } catch (error: Throwable) {
                return failureWithCallback(
                    callback,
                    request?.executionId.orEmpty(),
                    QuickJsRuntimeContract.ERROR_INTERNAL,
                    error.message ?: "QuickJS runtime failed",
                    startedAt,
                )
            } finally {
                runCatching { source?.close() }
                request?.executionId?.let { activeExecutions.remove(it, handle) }
                handle?.detachAndDestroy()
                if (gateAcquired) executionGate.release()
            }
        }
    }

    private fun enforcePluginCaller() {
        enforceCallingPermission(
            QuickJsRuntimeContract.PLUGIN_PERMISSION,
            "QuickJS runtime requires the AutoJs6 plugin permission",
        )
    }

    private fun runtimeInfo(): Bundle {
        val probe = runCatching { NativeQuickJsRuntime.nativeVersion() }
        val version = probe.getOrNull()
        val packagedAbis = applicationContext.packagedRuntimeAbis()
        val processAbi = Build.SUPPORTED_ABIS.firstOrNull { it in packagedAbis }
        return Bundle().apply {
            putInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, QuickJsRuntimeContract.PROTOCOL_VERSION)
            putBoolean(
                QuickJsRuntimeContract.KEY_RUNTIME_READY,
                version == QuickJsRuntimeContract.RUNTIME_VERSION && processAbi != null,
            )
            putString(QuickJsRuntimeContract.KEY_RUNTIME_VERSION, version)
            putString(QuickJsRuntimeContract.KEY_RUNTIME_REVISION, QuickJsRuntimeContract.RUNTIME_REVISION)
            putString(QuickJsRuntimeContract.KEY_NATIVE_LIBRARY, "libautojs_quickjs_runtime.so")
            putString(QuickJsRuntimeContract.KEY_PROCESS_NAME, currentProcessName())
            putString(QuickJsRuntimeContract.KEY_PROCESS_ABI, processAbi)
            putStringArray(QuickJsRuntimeContract.KEY_SUPPORTED_ABIS, packagedAbis)
            putLong(QuickJsRuntimeContract.KEY_MAX_SOURCE_BYTES, QuickJsRuntimeContract.MAX_SOURCE_BYTES)
            putLong(QuickJsRuntimeContract.KEY_MAX_OUTPUT_BYTES, QuickJsRuntimeContract.MAX_OUTPUT_BYTES)
            putLong(
                QuickJsRuntimeContract.KEY_MAX_MEMORY_BYTES,
                QuickJsRuntimeContract.MAX_MEMORY_LIMIT_BYTES,
            )
            putLong(
                QuickJsRuntimeContract.KEY_MAX_STACK_BYTES,
                QuickJsRuntimeContract.MAX_STACK_LIMIT_BYTES,
            )
            probe.exceptionOrNull()?.let {
                putString(QuickJsRuntimeContract.KEY_ERROR_MESSAGE, "QuickJS native runtime is unavailable")
            }
        }
    }

    private fun currentProcessName(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) return Application.getProcessName()
        return runCatching {
            File("/proc/self/cmdline").readText(Charsets.UTF_8).substringBefore('\u0000')
        }.getOrDefault("$packageName:quickjs_runtime")
    }

    private fun readSource(descriptor: ParcelFileDescriptor): ByteArray {
        val output = ByteArrayOutputStream()
        ParcelFileDescriptor.AutoCloseInputStream(descriptor).buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var total = 0L
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                total += count
                if (total > QuickJsRuntimeContract.MAX_SOURCE_BYTES) throw SourceTooLargeException()
                output.write(buffer, 0, count)
            }
        }
        return output.toByteArray()
    }

    private fun terminalResult(
        executionId: String,
        nativeResult: Array<String?>?,
        startedAt: Long,
    ): Bundle {
        val status = nativeResult?.getOrNull(0)
        val succeeded = status == NATIVE_STATUS_SUCCESS
        val errorCode = if (succeeded) null else when (status) {
            QuickJsRuntimeContract.ERROR_EVALUATION -> QuickJsRuntimeContract.ERROR_EVALUATION
            QuickJsRuntimeContract.ERROR_OUT_OF_MEMORY -> QuickJsRuntimeContract.ERROR_OUT_OF_MEMORY
            QuickJsRuntimeContract.ERROR_CANCELLED -> QuickJsRuntimeContract.ERROR_CANCELLED
            QuickJsRuntimeContract.ERROR_TIMEOUT -> QuickJsRuntimeContract.ERROR_TIMEOUT
            QuickJsRuntimeContract.ERROR_OUTPUT_LIMIT -> QuickJsRuntimeContract.ERROR_OUTPUT_LIMIT
            else -> QuickJsRuntimeContract.ERROR_INTERNAL
        }
        val defaultMessage = when (errorCode) {
            QuickJsRuntimeContract.ERROR_EVALUATION -> "QuickJS evaluation failed"
            QuickJsRuntimeContract.ERROR_OUT_OF_MEMORY -> "QuickJS exhausted its memory budget"
            QuickJsRuntimeContract.ERROR_CANCELLED -> "QuickJS execution was cancelled"
            QuickJsRuntimeContract.ERROR_TIMEOUT -> "QuickJS execution timed out"
            QuickJsRuntimeContract.ERROR_OUTPUT_LIMIT -> "QuickJS output exceeded its byte limit"
            null -> ""
            else -> "QuickJS runtime failed"
        }
        return Bundle().apply {
            putInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, QuickJsRuntimeContract.PROTOCOL_VERSION)
            putString(QuickJsRuntimeContract.KEY_EXECUTION_ID, executionId)
            putBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED, succeeded)
            putInt(QuickJsRuntimeContract.KEY_EXIT_CODE, if (succeeded) 0 else -1)
            putString(QuickJsRuntimeContract.KEY_STDOUT, "")
            putString(QuickJsRuntimeContract.KEY_STDERR, "")
            errorCode?.let { putString(QuickJsRuntimeContract.KEY_ERROR_CODE, it) }
            if (!succeeded) {
                putString(
                    QuickJsRuntimeContract.KEY_ERROR_MESSAGE,
                    boundedTerminalMessage(nativeResult?.getOrNull(1) ?: defaultMessage),
                )
            }
            putLong(
                QuickJsRuntimeContract.KEY_DURATION_MILLIS,
                SystemClock.elapsedRealtime() - startedAt,
            )
            putBoolean(
                QuickJsRuntimeContract.KEY_CANCELLED,
                errorCode == QuickJsRuntimeContract.ERROR_CANCELLED,
            )
            putBoolean(
                QuickJsRuntimeContract.KEY_TIMED_OUT,
                errorCode == QuickJsRuntimeContract.ERROR_TIMEOUT,
            )
        }
    }

    private fun failure(
        executionId: String,
        code: String,
        message: String,
        startedAt: Long,
        cancelled: Boolean = false,
    ): Bundle = Bundle().apply {
        putInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, QuickJsRuntimeContract.PROTOCOL_VERSION)
        putString(QuickJsRuntimeContract.KEY_EXECUTION_ID, executionId)
        putBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED, false)
        putInt(QuickJsRuntimeContract.KEY_EXIT_CODE, -1)
        putString(QuickJsRuntimeContract.KEY_STDOUT, "")
        putString(QuickJsRuntimeContract.KEY_STDERR, "")
        putString(QuickJsRuntimeContract.KEY_ERROR_CODE, code)
        putString(QuickJsRuntimeContract.KEY_ERROR_MESSAGE, boundedTerminalMessage(message))
        putLong(QuickJsRuntimeContract.KEY_DURATION_MILLIS, SystemClock.elapsedRealtime() - startedAt)
        putBoolean(QuickJsRuntimeContract.KEY_CANCELLED, cancelled)
        putBoolean(QuickJsRuntimeContract.KEY_TIMED_OUT, code == QuickJsRuntimeContract.ERROR_TIMEOUT)
    }

    private fun failureWithCallback(
        callback: IQuickJsRuntimeCallback?,
        executionId: String,
        code: String,
        message: String,
        startedAt: Long,
        cancelled: Boolean = false,
    ): Bundle = failure(executionId, code, message, startedAt, cancelled).also { result ->
        callback?.let { QuickJsOutputSink(it, executionId).finished(result) }
    }

    private fun boundedTerminalMessage(message: String): String {
        if (message.toByteArray(StandardCharsets.UTF_8).size <=
            QuickJsRuntimeContract.MAX_TERMINAL_MESSAGE_BYTES
        ) {
            return message
        }
        var low = 0
        var high = message.length
        while (low < high) {
            val middle = (low + high + 1) ushr 1
            if (message.substring(0, middle).toByteArray(StandardCharsets.UTF_8).size <=
                QuickJsRuntimeContract.MAX_TERMINAL_MESSAGE_BYTES
            ) {
                low = middle
            } else {
                high = middle - 1
            }
        }
        if (low > 0 && low < message.length &&
            message[low - 1].isHighSurrogate() && message[low].isLowSurrogate()
        ) {
            low--
        }
        return message.substring(0, low)
    }

    private fun rememberPendingCancellation(executionId: String) {
        prunePendingCancellations()
        if (pendingCancellations.size < MAX_PENDING_CANCELLATIONS) {
            pendingCancellations[executionId] = SystemClock.elapsedRealtime()
        }
    }

    private fun consumePendingCancellation(executionId: String): Boolean {
        val recordedAt = pendingCancellations.remove(executionId) ?: return false
        return SystemClock.elapsedRealtime() - recordedAt <= PENDING_CANCELLATION_TTL_MILLIS
    }

    private fun prunePendingCancellations() {
        val cutoff = SystemClock.elapsedRealtime() - PENDING_CANCELLATION_TTL_MILLIS
        pendingCancellations.entries.removeIf { it.value < cutoff }
    }

    private class QuickJsOutputSink(
        private val callback: IQuickJsRuntimeCallback,
        private val executionId: String,
    ) : NativeQuickJsRuntime.OutputSink {
        private val sequence = AtomicLong()
        private val closed = AtomicBoolean(false)

        fun started() = emit(QuickJsRuntimeContract.EVENT_STARTED)

        fun finished(terminal: Bundle) {
            if (closed.compareAndSet(false, true)) {
                runCatching {
                    emit(QuickJsRuntimeContract.EVENT_FINISHED, terminal = terminal, allowClosed = true)
                }
            }
        }

        override fun onOutput(stream: Int, utf8: ByteArray) {
            if (closed.get() || utf8.size > QuickJsRuntimeContract.MAX_CALLBACK_CHUNK_BYTES) return
            val type = when (stream) {
                NativeQuickJsRuntime.STREAM_STDOUT -> QuickJsRuntimeContract.EVENT_STDOUT
                NativeQuickJsRuntime.STREAM_STDERR -> QuickJsRuntimeContract.EVENT_STDERR
                else -> return
            }
            emit(type, text = utf8.toString(Charsets.UTF_8))
        }

        private fun emit(
            type: String,
            text: String? = null,
            terminal: Bundle? = null,
            allowClosed: Boolean = false,
        ) {
            if (closed.get() && !allowClosed) return
            callback.onEvent(Bundle().apply {
                putInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, QuickJsRuntimeContract.PROTOCOL_VERSION)
                putString(QuickJsRuntimeContract.KEY_EXECUTION_ID, executionId)
                putString(QuickJsRuntimeContract.KEY_EVENT_TYPE, type)
                putLong(QuickJsRuntimeContract.KEY_SEQUENCE, sequence.getAndIncrement())
                text?.let { putString(QuickJsRuntimeContract.KEY_TEXT, it) }
                terminal?.let(::putAll)
            })
        }
    }

    private class ExecutionHandle {
        private val cancelled = AtomicBoolean(false)
        private val lock = Any()
        private var nativeHandle = 0L

        fun attach(handle: Long) {
            synchronized(lock) {
                check(nativeHandle == 0L) { "Native QuickJS handle is already attached" }
                nativeHandle = handle
                if (cancelled.get()) NativeQuickJsRuntime.nativeCancel(handle)
            }
        }

        fun cancel(): Boolean {
            val changed = cancelled.compareAndSet(false, true)
            synchronized(lock) {
                if (nativeHandle != 0L) NativeQuickJsRuntime.nativeCancel(nativeHandle)
            }
            return changed
        }

        fun isCancelled(): Boolean = cancelled.get()

        fun detachAndDestroy() {
            val handle = synchronized(lock) {
                val current = nativeHandle
                nativeHandle = 0L
                current
            }
            if (handle != 0L) NativeQuickJsRuntime.nativeDestroyExecution(handle)
        }
    }

    private class SourceTooLargeException : IllegalArgumentException(
        "Source exceeds ${QuickJsRuntimeContract.MAX_SOURCE_BYTES} bytes",
    )

    private companion object {
        const val NATIVE_STATUS_SUCCESS = "SUCCESS"
        const val MAX_PENDING_CANCELLATIONS = 64
        const val PENDING_CANCELLATION_TTL_MILLIS = 60_000L
    }
}
