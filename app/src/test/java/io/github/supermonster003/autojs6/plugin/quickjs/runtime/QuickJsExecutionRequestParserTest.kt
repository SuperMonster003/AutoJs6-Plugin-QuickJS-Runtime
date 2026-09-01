package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import org.autojs.plugin.quickjs.runtime.api.QuickJsRuntimeContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickJsExecutionRequestParserTest {
    @Test
    fun defaultsProduceBoundedScriptRequest() {
        val request = QuickJsExecutionRequestParser.normalize(values())

        assertEquals("run-1", request.executionId)
        assertEquals("script.quickjs.js", request.sourceName)
        assertEquals(QuickJsRuntimeContract.SOURCE_MODE_SCRIPT, request.sourceMode)
        assertEquals(QuickJsRuntimeContract.DEFAULT_TIMEOUT_MILLIS, request.timeoutMillis)
        assertEquals(QuickJsRuntimeContract.DEFAULT_MEMORY_LIMIT_BYTES, request.memoryLimitBytes)
        assertEquals(QuickJsRuntimeContract.DEFAULT_STACK_LIMIT_BYTES, request.stackLimitBytes)
    }

    @Test
    fun moduleSuffixSelectsModuleMode() {
        val request = QuickJsExecutionRequestParser.normalize(
            values(sourceName = "entry.quickjs.mjs"),
        )

        assertEquals(QuickJsRuntimeContract.SOURCE_MODE_MODULE, request.sourceMode)
    }

    @Test
    fun explicitModeOverridesSuffix() {
        val request = QuickJsExecutionRequestParser.normalize(
            values(
                sourceName = "entry.quickjs.mjs",
                sourceMode = QuickJsRuntimeContract.SOURCE_MODE_SCRIPT,
            ),
        )

        assertEquals(QuickJsRuntimeContract.SOURCE_MODE_SCRIPT, request.sourceMode)
    }

    @Test
    fun supplementaryUnicodeSourceNameIsMeasuredAsUtf8() {
        val accepted = "\uD83E\uDDEA".repeat(100)
        val request = QuickJsExecutionRequestParser.normalize(values(sourceName = accepted))
        assertEquals(accepted, request.sourceName)

        val rejected = "\uD83E\uDDEA".repeat(QuickJsRuntimeContract.MAX_SOURCE_NAME_BYTES / 4 + 1)
        assertThrows(IllegalArgumentException::class.java) {
            QuickJsExecutionRequestParser.normalize(values(sourceName = rejected))
        }
    }

    @Test
    fun invalidIdentifiersModesAndLimitsAreRejected() {
        listOf("", "contains space", "x".repeat(129)).forEach { executionId ->
            assertThrows(IllegalArgumentException::class.java) {
                QuickJsExecutionRequestParser.normalize(values(executionId = executionId))
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            QuickJsExecutionRequestParser.normalize(values(sourceMode = "auto"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            QuickJsExecutionRequestParser.normalize(values(timeoutMillis = 0L))
        }
        assertThrows(IllegalArgumentException::class.java) {
            QuickJsExecutionRequestParser.normalize(
                values(memoryLimitBytes = QuickJsRuntimeContract.MAX_MEMORY_LIMIT_BYTES + 1L),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            QuickJsExecutionRequestParser.normalize(values(outputByteLimit = 0L))
        }
        listOf("\uD83E", "\uDDEA").forEach { invalidUnicode ->
            assertThrows(IllegalArgumentException::class.java) {
                QuickJsExecutionRequestParser.normalize(values(sourceName = invalidUnicode))
            }
        }
    }

    @Test
    fun executionIdentifierGrammarIsStable() {
        assertTrue(QuickJsExecutionRequestParser.isValidExecutionId("engine-1.a_b"))
    }

    private fun values(
        protocolVersion: Int = QuickJsRuntimeContract.PROTOCOL_VERSION,
        executionId: String? = "run-1",
        sourceName: String? = null,
        sourceMode: String? = null,
        timeoutMillis: Long = QuickJsRuntimeContract.DEFAULT_TIMEOUT_MILLIS,
        memoryLimitBytes: Long = QuickJsRuntimeContract.DEFAULT_MEMORY_LIMIT_BYTES,
        stackLimitBytes: Long = QuickJsRuntimeContract.DEFAULT_STACK_LIMIT_BYTES,
        outputByteLimit: Long = QuickJsRuntimeContract.MAX_OUTPUT_BYTES,
    ) = QuickJsRequestValues(
        protocolVersion = protocolVersion,
        executionId = executionId,
        sourceName = sourceName,
        sourceMode = sourceMode,
        timeoutMillis = timeoutMillis,
        memoryLimitBytes = memoryLimitBytes,
        stackLimitBytes = stackLimitBytes,
        outputByteLimit = outputByteLimit,
    )
}
