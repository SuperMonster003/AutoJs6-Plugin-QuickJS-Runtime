package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickJsPluginMetadataTest {
    @Test
    fun identityMatchesRepositoryContract() {
        val identity = QuickJsPluginMetadata.identity()

        assertEquals("quickjs-runtime", identity.id)
        assertEquals("quickjs", identity.engine)
        assertEquals("quickjs-2026-06-04", identity.variant)
        assertEquals(5279L, identity.requiredHostVersion)
        assertEquals(1, identity.protocolVersion)
        assertEquals("2026-06-04", identity.runtimeVersion)
        assertTrue(identity.runtimeRevision.endsWith("3d5e064e9dd67c70f7962836505a7fa067bf0a4e"))
    }

    @Test
    fun distributedAbiInventoryMatchesNativeBuild() {
        assertEquals(
            listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86"),
            DISTRIBUTED_ABIS.toList(),
        )
        assertTrue(INSTRUCTION_REFERENCE.startsWith("@raw/"))
    }
}
