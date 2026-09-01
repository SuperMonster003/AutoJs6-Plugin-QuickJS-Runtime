package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.quickjs.runtime.api.IQuickJsRuntimeCallback
import org.autojs.plugin.quickjs.runtime.api.IQuickJsRuntimePlugin
import org.autojs.plugin.quickjs.runtime.api.QuickJsPluginActions
import org.autojs.plugin.quickjs.runtime.api.QuickJsPluginCapabilityKeys
import org.autojs.plugin.quickjs.runtime.api.QuickJsPluginIds
import org.autojs.plugin.quickjs.runtime.api.QuickJsRuntimeContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.RandomAccessFile
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class QuickJsRuntimeInstrumentedTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun assertRequestedApiLevel() {
        InstrumentationRegistry.getArguments()
            .getString(REQUIRED_API_LEVEL_ARGUMENT)
            ?.toIntOrNull()
            ?.let { expected ->
                assertEquals(
                    "Instrumentation ran on API ${Build.VERSION.SDK_INT}, but API $expected was required",
                    expected,
                    Build.VERSION.SDK_INT,
                )
            }
    }

    @Test
    fun discoveryMetadataPrewarmAndLanguageRoundTrips() {
        withBoundRuntime { runtime ->
            val info = runtime.info
            @Suppress("DEPRECATION")
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val installedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            assertEquals(context.getString(R.string.app_name), info.name)
            assertEquals(context.getString(R.string.plugin_description), info.description)
            assertEquals(context.getString(R.string.plugin_author), info.author)
            assertEquals(QuickJsPluginIds.ID, info.id)
            assertEquals(QuickJsPluginIds.ENGINE, info.engine)
            assertEquals(QuickJsPluginIds.VARIANT_QUICKJS_2026_06_04, info.variant)
            assertEquals(packageInfo.versionName, info.versionName)
            assertEquals(installedVersionCode, info.versionCode)
            assertEquals("@raw/plugin_instruction", info.instruction)
            assertTrue(info.versionDate?.isNotBlank() == true)
            val packagedAbis = context.packagedRuntimeAbis().toSet()
            assertTrue(packagedAbis.isNotEmpty())
            assertTrue(packagedAbis.all { it in DISTRIBUTED_ABIS })
            assertEquals(packagedAbis, info.supportedAbis.orEmpty().toSet())
            assertEquals(
                QuickJsRuntimeContract.REQUIRES_HOST_VERSION,
                requireNotNull(info.capabilities)
                    .getLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION),
            )
            assertEquals(
                QuickJsRuntimeContract.PROTOCOL_VERSION,
                requireNotNull(info.capabilities)
                    .getInt(QuickJsPluginCapabilityKeys.CONTRACT_VERSION),
            )
            assertTrue(requireNotNull(info.capabilities).getBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_MODULES))
            assertTrue(requireNotNull(info.capabilities).getBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_CANCELLATION))
            assertTrue(requireNotNull(info.capabilities).getBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_STREAMING_OUTPUT))
            assertTrue(requireNotNull(info.capabilities).getBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_MEMORY_LIMITS))

            repeat(2) {
                assertRuntimeProbe(runtime.prewarmRuntime(), packagedAbis)
            }

            val script = runSource(
                runtime = runtime,
                sourceName = "language.quickjs.js",
                sourceText = """
                    console.log("answer=" + (21 * 2));
                    console.log("unicode=中文🧪");
                    console.log("bigint=" + String(2n ** 63n));
                    console.warn("stderr-ok");
                """.trimIndent(),
            )
            assertTrue(script.failureDescription(), script.result.getBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED))
            assertTrue(script.stdout.contains("answer=42"))
            assertTrue(script.stdout.contains("中文🧪"))
            assertTrue(script.stdout.contains("9223372036854775808"))
            assertTrue(script.stderr.contains("stderr-ok"))
            assertTrue(script.result.getString(QuickJsRuntimeContract.KEY_STDOUT).isNullOrEmpty())
            assertTrue(script.result.getString(QuickJsRuntimeContract.KEY_STDERR).isNullOrEmpty())

            val module = runSource(
                runtime = runtime,
                sourceName = "module.quickjs.mjs",
                sourceMode = QuickJsRuntimeContract.SOURCE_MODE_MODULE,
                sourceText = """
                    const value = await Promise.resolve(7);
                    console.log(`module=${'$'}{value}`);
                    export { value };
                """.trimIndent(),
            )
            assertTrue(module.failureDescription(), module.result.getBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED))
            assertTrue(module.stdout.contains("module=7"))

            val exception = runSource(
                runtime = runtime,
                sourceName = "exception.quickjs.js",
                sourceText = "throw new Error('expected-failure');",
            ).result
            assertFalse(exception.getBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED))
            assertEquals(
                QuickJsRuntimeContract.ERROR_EVALUATION,
                exception.getString(QuickJsRuntimeContract.KEY_ERROR_CODE),
            )
            assertTrue(exception.getString(QuickJsRuntimeContract.KEY_ERROR_MESSAGE).orEmpty().contains("expected-failure"))
        }
    }

    @Test
    fun timeoutCancellationOutputAndInputLimitsAreEnforced() {
        withBoundRuntime { runtime ->
            val timeoutStartedAt = SystemClock.elapsedRealtime()
            val timedOut = runSource(
                runtime = runtime,
                sourceName = "timeout.quickjs.js",
                sourceText = "while (true) {}",
                timeoutMillis = 750L,
            ).result
            assertFalse(timedOut.getBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED))
            assertTrue(timedOut.getBoolean(QuickJsRuntimeContract.KEY_TIMED_OUT))
            assertEquals(QuickJsRuntimeContract.ERROR_TIMEOUT, timedOut.getString(QuickJsRuntimeContract.KEY_ERROR_CODE))
            assertTrue(SystemClock.elapsedRealtime() - timeoutStartedAt < 10_000L)

            val outputLimited = runSource(
                runtime = runtime,
                sourceName = "output-limit.quickjs.js",
                sourceText = "while (true) console.log('x'.repeat(4096));",
                timeoutMillis = 20_000L,
                outputByteLimit = 128L,
            ).result
            assertFalse(outputLimited.getBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED))
            assertEquals(
                QuickJsRuntimeContract.ERROR_OUTPUT_LIMIT,
                outputLimited.getString(QuickJsRuntimeContract.KEY_ERROR_CODE),
            )

            val cancelledBeforeDispatchId = "cancel-before-${UUID.randomUUID()}"
            assertTrue(runtime.cancelScript(cancelledBeforeDispatchId))
            val cancelledBeforeDispatch = runSource(
                runtime = runtime,
                sourceName = "cancel-before.quickjs.js",
                sourceText = "throw new Error('must-not-run');",
                executionId = cancelledBeforeDispatchId,
                expectStarted = false,
            ).result
            assertTrue(cancelledBeforeDispatch.getBoolean(QuickJsRuntimeContract.KEY_CANCELLED))
            assertEquals(
                QuickJsRuntimeContract.ERROR_CANCELLED,
                cancelledBeforeDispatch.getString(QuickJsRuntimeContract.KEY_ERROR_CODE),
            )

            val executionId = "cancel-${UUID.randomUUID()}"
            val started = CountDownLatch(1)
            val executor = Executors.newSingleThreadExecutor()
            try {
                val future = executor.submit<CapturedRun> {
                    runSource(
                        runtime = runtime,
                        sourceName = "cancel.quickjs.js",
                        sourceText = "while (true) {}",
                        executionId = executionId,
                        timeoutMillis = 60_000L,
                        startedSignal = started,
                    )
                }
                assertTrue("QuickJS execution did not start", started.await(20, TimeUnit.SECONDS))
                assertTrue(runtime.cancelScript(executionId))
                val captured = future.get(20, TimeUnit.SECONDS)
                val cancelled = captured.result
                assertFalse(captured.failureDescription(), cancelled.getBoolean(QuickJsRuntimeContract.KEY_SUCCEEDED))
                assertTrue(captured.failureDescription(), cancelled.getBoolean(QuickJsRuntimeContract.KEY_CANCELLED))
                assertEquals(
                    QuickJsRuntimeContract.ERROR_CANCELLED,
                    cancelled.getString(QuickJsRuntimeContract.KEY_ERROR_CODE),
                )
            } finally {
                executor.shutdownNow()
            }

            val oversized = File.createTempFile("quickjs-test-oversized-", ".js", context.cacheDir)
            try {
                RandomAccessFile(oversized, "rw").use { file ->
                    file.setLength(QuickJsRuntimeContract.MAX_SOURCE_BYTES + 1L)
                }
                val oversizedResult = ParcelFileDescriptor.open(
                    oversized,
                    ParcelFileDescriptor.MODE_READ_ONLY,
                ).use { descriptor ->
                    runtime.runScript(
                        baseRequest("oversized-${UUID.randomUUID()}", "oversized.quickjs.js"),
                        descriptor,
                        noOpCallback(),
                    )
                }
                assertEquals(
                    QuickJsRuntimeContract.ERROR_SOURCE_TOO_LARGE,
                    oversizedResult.getString(QuickJsRuntimeContract.KEY_ERROR_CODE),
                )
            } finally {
                oversized.delete()
            }

            val invalid = runtime.runScript(
                Bundle().apply {
                    putInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, QuickJsRuntimeContract.PROTOCOL_VERSION + 1)
                },
                null,
                null,
            )
            assertEquals(
                QuickJsRuntimeContract.ERROR_INVALID_REQUEST,
                invalid.getString(QuickJsRuntimeContract.KEY_ERROR_CODE),
            )
        }
    }

    @Test
    fun manifestPublishesWakeInfoAndRuntimeContracts() {
        val runtimeIntent = Intent(QuickJsPluginActions.RUNTIME)
            .addCategory(QuickJsPluginActions.CATEGORY)
            .setPackage(context.packageName)
        @Suppress("DEPRECATION")
        val runtimeService = context.packageManager.queryIntentServices(runtimeIntent, 0).single().serviceInfo
        assertTrue(runtimeService.exported)
        assertEquals(QuickJsRuntimeContract.PLUGIN_PERMISSION, runtimeService.permission)
        assertTrue(runtimeService.processName.endsWith(":quickjs_runtime"))

        val infoIntent = Intent(QuickJsPluginActions.INFO)
            .addCategory(QuickJsPluginActions.CATEGORY)
            .setPackage(context.packageName)
        @Suppress("DEPRECATION")
        val infoService = context.packageManager.queryIntentServices(infoIntent, 0).single().serviceInfo
        assertTrue(infoService.exported)
        assertEquals(QuickJsRuntimeContract.PLUGIN_PERMISSION, infoService.permission)

        val wakeIntent = Intent("org.autojs.plugin.action.WAKE")
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setPackage(context.packageName)
        @Suppress("DEPRECATION")
        val wakeActivity = context.packageManager.queryIntentActivities(wakeIntent, 0).single().activityInfo
        assertTrue(wakeActivity.exported)
        assertEquals(QuickJsRuntimeContract.PLUGIN_PERMISSION, wakeActivity.permission)

        @Suppress("DEPRECATION")
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            android.content.pm.PackageManager.GET_META_DATA,
        )
        assertEquals(".WakeActivity", appInfo.metaData.getString("org.autojs.plugin.WAKE_ACTIVITY"))
    }

    private fun runSource(
        runtime: IQuickJsRuntimePlugin,
        sourceName: String,
        sourceText: String,
        sourceMode: String? = null,
        executionId: String = "instrumentation-${UUID.randomUUID()}",
        timeoutMillis: Long = 30_000L,
        outputByteLimit: Long = 1024L * 1024L,
        startedSignal: CountDownLatch? = null,
        expectStarted: Boolean = true,
    ): CapturedRun {
        val sourceFile = File.createTempFile("quickjs-test-", ".js", context.cacheDir)
        sourceFile.writeText(sourceText, Charsets.UTF_8)
        val finished = CountDownLatch(1)
        val events = mutableListOf<String>()
        val stdout = StringBuilder()
        val stderr = StringBuilder()
        val callback = object : IQuickJsRuntimeCallback.Stub() {
            override fun onEvent(event: Bundle?) {
                event ?: return
                assertEquals(
                    QuickJsRuntimeContract.PROTOCOL_VERSION,
                    event.getInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, -1),
                )
                assertEquals(executionId, event.getString(QuickJsRuntimeContract.KEY_EXECUTION_ID))
                val type = event.getString(QuickJsRuntimeContract.KEY_EVENT_TYPE).orEmpty()
                synchronized(events) { events += type }
                when (type) {
                    QuickJsRuntimeContract.EVENT_STARTED -> startedSignal?.countDown()
                    QuickJsRuntimeContract.EVENT_STDOUT -> synchronized(stdout) {
                        stdout.append(event.getString(QuickJsRuntimeContract.KEY_TEXT).orEmpty())
                    }
                    QuickJsRuntimeContract.EVENT_STDERR -> synchronized(stderr) {
                        stderr.append(event.getString(QuickJsRuntimeContract.KEY_TEXT).orEmpty())
                    }
                    QuickJsRuntimeContract.EVENT_FINISHED -> finished.countDown()
                }
            }
        }
        return try {
            ParcelFileDescriptor.open(sourceFile, ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                runtime.runScript(
                    baseRequest(executionId, sourceName).apply {
                        putLong(QuickJsRuntimeContract.KEY_TIMEOUT_MILLIS, timeoutMillis)
                        putLong(QuickJsRuntimeContract.KEY_OUTPUT_BYTE_LIMIT, outputByteLimit)
                        sourceMode?.let { putString(QuickJsRuntimeContract.KEY_SOURCE_MODE, it) }
                    },
                    descriptor,
                    callback,
                )
            }.also { result ->
                assertEquals(
                    QuickJsRuntimeContract.PROTOCOL_VERSION,
                    result.getInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, -1),
                )
                assertTrue("Finished callback was not delivered", finished.await(10, TimeUnit.SECONDS))
                synchronized(events) {
                    assertEquals(expectStarted, QuickJsRuntimeContract.EVENT_STARTED in events)
                    assertTrue(QuickJsRuntimeContract.EVENT_FINISHED in events)
                }
            }.let { result ->
                CapturedRun(
                    result = result,
                    stdout = synchronized(stdout) { stdout.toString() },
                    stderr = synchronized(stderr) { stderr.toString() },
                )
            }
        } finally {
            sourceFile.delete()
        }
    }

    private fun baseRequest(executionId: String, sourceName: String) = Bundle().apply {
        putInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, QuickJsRuntimeContract.PROTOCOL_VERSION)
        putString(QuickJsRuntimeContract.KEY_EXECUTION_ID, executionId)
        putString(QuickJsRuntimeContract.KEY_SOURCE_NAME, sourceName)
    }

    private fun noOpCallback() = object : IQuickJsRuntimeCallback.Stub() {
        override fun onEvent(event: Bundle?) = Unit
    }

    private fun assertRuntimeProbe(probe: Bundle, packagedAbis: Set<String>) {
        assertTrue(
            probe.getString(QuickJsRuntimeContract.KEY_ERROR_MESSAGE).orEmpty(),
            probe.getBoolean(QuickJsRuntimeContract.KEY_RUNTIME_READY),
        )
        assertEquals(QuickJsRuntimeContract.RUNTIME_VERSION, probe.getString(QuickJsRuntimeContract.KEY_RUNTIME_VERSION))
        assertEquals(QuickJsRuntimeContract.RUNTIME_REVISION, probe.getString(QuickJsRuntimeContract.KEY_RUNTIME_REVISION))
        assertEquals("libautojs_quickjs_runtime.so", probe.getString(QuickJsRuntimeContract.KEY_NATIVE_LIBRARY))
        assertTrue(probe.getString(QuickJsRuntimeContract.KEY_PROCESS_NAME).orEmpty().endsWith(":quickjs_runtime"))
        assertEquals(
            packagedAbis,
            probe.getStringArray(QuickJsRuntimeContract.KEY_SUPPORTED_ABIS).orEmpty().toSet(),
        )
        assertEquals(
            Build.SUPPORTED_ABIS.first(packagedAbis::contains),
            probe.getString(QuickJsRuntimeContract.KEY_PROCESS_ABI),
        )
        assertEquals(QuickJsRuntimeContract.MAX_SOURCE_BYTES, probe.getLong(QuickJsRuntimeContract.KEY_MAX_SOURCE_BYTES))
        assertEquals(QuickJsRuntimeContract.MAX_OUTPUT_BYTES, probe.getLong(QuickJsRuntimeContract.KEY_MAX_OUTPUT_BYTES))
        assertEquals(
            QuickJsRuntimeContract.MAX_MEMORY_LIMIT_BYTES,
            probe.getLong(QuickJsRuntimeContract.KEY_MAX_MEMORY_BYTES),
        )
        assertEquals(
            QuickJsRuntimeContract.MAX_STACK_LIMIT_BYTES,
            probe.getLong(QuickJsRuntimeContract.KEY_MAX_STACK_BYTES),
        )
    }

    private fun withBoundRuntime(block: (IQuickJsRuntimePlugin) -> Unit) {
        val discovery = Intent(QuickJsPluginActions.RUNTIME)
            .addCategory(QuickJsPluginActions.CATEGORY)
            .setPackage(context.packageName)
        @Suppress("DEPRECATION")
        val matches = context.packageManager.queryIntentServices(discovery, 0)
        assertEquals("QuickJS runtime discovery must resolve exactly one service", 1, matches.size)
        val serviceInfo = matches.single().serviceInfo
        val latch = CountDownLatch(1)
        val binder = AtomicReference<IBinder?>()
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                binder.set(service)
                latch.countDown()
            }

            override fun onServiceDisconnected(name: ComponentName?) = Unit
            override fun onNullBinding(name: ComponentName?) = latch.countDown()
            override fun onBindingDied(name: ComponentName?) = latch.countDown()
        }
        val explicit = Intent(discovery).setComponent(ComponentName(serviceInfo.packageName, serviceInfo.name))
        assertTrue(context.bindService(explicit, connection, Context.BIND_AUTO_CREATE))
        try {
            assertTrue(latch.await(20, TimeUnit.SECONDS))
            assertNotNull(binder.get())
            assertEquals(QuickJsRuntimeContract.BINDER_DESCRIPTOR, binder.get()?.interfaceDescriptor)
            block(IQuickJsRuntimePlugin.Stub.asInterface(binder.get()))
        } finally {
            context.unbindService(connection)
        }
    }

    private data class CapturedRun(
        val result: Bundle,
        val stdout: String,
        val stderr: String,
    ) {
        fun failureDescription(): String = buildString {
            append("code=")
            append(result.getString(QuickJsRuntimeContract.KEY_ERROR_CODE))
            append(", cancelled=")
            append(result.getBoolean(QuickJsRuntimeContract.KEY_CANCELLED))
            append(", timedOut=")
            append(result.getBoolean(QuickJsRuntimeContract.KEY_TIMED_OUT))
            append(", message=")
            append(result.getString(QuickJsRuntimeContract.KEY_ERROR_MESSAGE).orEmpty())
            if (stderr.isNotBlank()) append("; stderr: $stderr")
            if (stdout.isNotBlank()) append("; stdout: $stdout")
        }
    }

    private companion object {
        const val REQUIRED_API_LEVEL_ARGUMENT = "requiredApiLevel"
    }
}
