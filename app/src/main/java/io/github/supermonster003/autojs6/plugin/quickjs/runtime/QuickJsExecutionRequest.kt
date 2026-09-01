package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import android.os.Bundle
import org.autojs.plugin.quickjs.runtime.api.QuickJsRuntimeContract

internal data class QuickJsExecutionRequest(
    val executionId: String,
    val sourceName: String,
    val sourceMode: String,
    val timeoutMillis: Long,
    val memoryLimitBytes: Long,
    val stackLimitBytes: Long,
    val outputByteLimit: Long,
)

internal data class QuickJsRequestValues(
    val protocolVersion: Int,
    val executionId: String?,
    val sourceName: String?,
    val sourceMode: String?,
    val timeoutMillis: Long,
    val memoryLimitBytes: Long,
    val stackLimitBytes: Long,
    val outputByteLimit: Long,
)

internal object QuickJsExecutionRequestParser {
    private val executionIdPattern = Regex("[A-Za-z0-9._-]{1,128}")
    private val sourceModes = setOf(
        QuickJsRuntimeContract.SOURCE_MODE_SCRIPT,
        QuickJsRuntimeContract.SOURCE_MODE_MODULE,
    )
    private val allowedKeys = setOf(
        QuickJsRuntimeContract.KEY_PROTOCOL_VERSION,
        QuickJsRuntimeContract.KEY_EXECUTION_ID,
        QuickJsRuntimeContract.KEY_SOURCE_NAME,
        QuickJsRuntimeContract.KEY_SOURCE_MODE,
        QuickJsRuntimeContract.KEY_TIMEOUT_MILLIS,
        QuickJsRuntimeContract.KEY_MEMORY_LIMIT_BYTES,
        QuickJsRuntimeContract.KEY_STACK_LIMIT_BYTES,
        QuickJsRuntimeContract.KEY_OUTPUT_BYTE_LIMIT,
    )

    fun isValidExecutionId(value: String): Boolean = executionIdPattern.matches(value)

    fun parse(request: Bundle?): QuickJsExecutionRequest {
        requireNotNull(request) { "Request is missing" }
        require(request.keySet().all(allowedKeys::contains)) { "Request contains unsupported keys" }
        return normalize(
            QuickJsRequestValues(
                protocolVersion = request.getInt(QuickJsRuntimeContract.KEY_PROTOCOL_VERSION, -1),
                executionId = request.getString(QuickJsRuntimeContract.KEY_EXECUTION_ID),
                sourceName = request.getString(QuickJsRuntimeContract.KEY_SOURCE_NAME),
                sourceMode = request.getString(QuickJsRuntimeContract.KEY_SOURCE_MODE),
                timeoutMillis = request.getLong(
                    QuickJsRuntimeContract.KEY_TIMEOUT_MILLIS,
                    QuickJsRuntimeContract.DEFAULT_TIMEOUT_MILLIS,
                ),
                memoryLimitBytes = request.getLong(
                    QuickJsRuntimeContract.KEY_MEMORY_LIMIT_BYTES,
                    QuickJsRuntimeContract.DEFAULT_MEMORY_LIMIT_BYTES,
                ),
                stackLimitBytes = request.getLong(
                    QuickJsRuntimeContract.KEY_STACK_LIMIT_BYTES,
                    QuickJsRuntimeContract.DEFAULT_STACK_LIMIT_BYTES,
                ),
                outputByteLimit = request.getLong(
                    QuickJsRuntimeContract.KEY_OUTPUT_BYTE_LIMIT,
                    QuickJsRuntimeContract.MAX_OUTPUT_BYTES,
                ),
            ),
        )
    }

    fun normalize(values: QuickJsRequestValues): QuickJsExecutionRequest {
        require(values.protocolVersion == QuickJsRuntimeContract.PROTOCOL_VERSION) {
            "Unsupported QuickJS runtime protocol version"
        }
        val executionId = values.executionId.orEmpty()
        require(isValidExecutionId(executionId)) { "Invalid execution ID" }
        val sourceName = values.sourceName?.takeIf(String::isNotBlank) ?: "script.quickjs.js"
        require('\u0000' !in sourceName) { "Source name cannot contain NUL" }
        require(!sourceName.hasUnpairedSurrogate()) { "Source name contains invalid Unicode" }
        require(sourceName.toByteArray(Charsets.UTF_8).size <= QuickJsRuntimeContract.MAX_SOURCE_NAME_BYTES) {
            "Source name is too long"
        }
        val sourceMode = values.sourceMode ?: if (sourceName.endsWith(".mjs", ignoreCase = true)) {
            QuickJsRuntimeContract.SOURCE_MODE_MODULE
        } else {
            QuickJsRuntimeContract.SOURCE_MODE_SCRIPT
        }
        require(sourceMode in sourceModes) { "Unsupported QuickJS source mode" }
        require(values.timeoutMillis in 1L..QuickJsRuntimeContract.MAX_TIMEOUT_MILLIS) {
            "Invalid execution timeout"
        }
        require(values.memoryLimitBytes in 1L..QuickJsRuntimeContract.MAX_MEMORY_LIMIT_BYTES) {
            "Invalid memory limit"
        }
        require(values.stackLimitBytes in 1L..QuickJsRuntimeContract.MAX_STACK_LIMIT_BYTES) {
            "Invalid stack limit"
        }
        require(values.outputByteLimit in 1L..QuickJsRuntimeContract.MAX_OUTPUT_BYTES) {
            "Invalid output byte limit"
        }
        return QuickJsExecutionRequest(
            executionId = executionId,
            sourceName = sourceName,
            sourceMode = sourceMode,
            timeoutMillis = values.timeoutMillis,
            memoryLimitBytes = values.memoryLimitBytes,
            stackLimitBytes = values.stackLimitBytes,
            outputByteLimit = values.outputByteLimit,
        )
    }

    private fun String.hasUnpairedSurrogate(): Boolean {
        var index = 0
        while (index < length) {
            val character = this[index]
            when {
                character.isHighSurrogate() -> {
                    if (index + 1 >= length || !this[index + 1].isLowSurrogate()) return true
                    index += 2
                }
                character.isLowSurrogate() -> return true
                else -> index++
            }
        }
        return false
    }
}
