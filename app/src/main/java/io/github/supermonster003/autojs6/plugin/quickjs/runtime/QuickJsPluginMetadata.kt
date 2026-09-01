package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import android.content.Context
import android.os.Build
import android.os.Bundle
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.quickjs.runtime.api.QuickJsPluginCapabilityKeys
import org.autojs.plugin.quickjs.runtime.api.QuickJsPluginIds
import org.autojs.plugin.quickjs.runtime.api.QuickJsRuntimeContract
import java.util.zip.ZipFile

internal val DISTRIBUTED_ABIS = arrayOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
internal const val INSTRUCTION_REFERENCE = "@raw/plugin_instruction"

internal data class QuickJsIdentity(
    val id: String,
    val engine: String,
    val variant: String,
    val requiredHostVersion: Long,
    val protocolVersion: Int,
    val runtimeVersion: String,
    val runtimeRevision: String,
)

internal object QuickJsPluginMetadata {
    fun identity() = QuickJsIdentity(
        id = QuickJsPluginIds.ID,
        engine = QuickJsPluginIds.ENGINE,
        variant = QuickJsPluginIds.VARIANT_QUICKJS_2026_06_04,
        requiredHostVersion = QuickJsRuntimeContract.REQUIRES_HOST_VERSION,
        protocolVersion = QuickJsRuntimeContract.PROTOCOL_VERSION,
        runtimeVersion = QuickJsRuntimeContract.RUNTIME_VERSION,
        runtimeRevision = QuickJsRuntimeContract.RUNTIME_REVISION,
    )
}

internal fun Context.packagedRuntimeAbis(): Array<String> {
    val archivePaths = buildList {
        add(applicationInfo.sourceDir)
        addAll(applicationInfo.splitSourceDirs.orEmpty())
    }
    val packaged = archivePaths.flatMapTo(linkedSetOf()) { archivePath ->
        runCatching {
            ZipFile(archivePath).use { archive ->
                archive.entries().asSequence()
                    .map { it.name }
                    .mapNotNull { name ->
                        DISTRIBUTED_ABIS.firstOrNull { abi ->
                            name == "lib/$abi/libautojs_quickjs_runtime.so"
                        }
                    }
                    .toList()
            }
        }.getOrDefault(emptyList())
    }
    return DISTRIBUTED_ABIS.filter(packaged::contains).toTypedArray()
}

internal fun Context.quickJsPluginInfo(): PluginInfo {
    val appContext = applicationContext
    val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
    val installedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
    }
    return PluginInfo().apply {
        name = getString(R.string.app_name)
        description = getString(R.string.plugin_description)
        instruction = INSTRUCTION_REFERENCE
        author = getString(R.string.plugin_author)
        collaborators = arrayOf("Fabrice Bellard", "Charlie Gordon")
        versionName = packageInfo.versionName.orEmpty()
        versionCode = installedVersionCode
        versionDate = getString(R.string.plugin_version_date)
        id = QuickJsPluginIds.ID
        engine = QuickJsPluginIds.ENGINE
        variant = QuickJsPluginIds.VARIANT_QUICKJS_2026_06_04
        supportedAbis = appContext.packagedRuntimeAbis()
        capabilities = Bundle().apply {
            putLong(
                PluginCapabilityKeys.REQUIRES_HOST_VERSION,
                QuickJsRuntimeContract.REQUIRES_HOST_VERSION,
            )
            putInt(
                QuickJsPluginCapabilityKeys.CONTRACT_VERSION,
                QuickJsRuntimeContract.PROTOCOL_VERSION,
            )
            putString(
                QuickJsPluginCapabilityKeys.RUNTIME_VERSION,
                QuickJsRuntimeContract.RUNTIME_VERSION,
            )
            putString(
                QuickJsPluginCapabilityKeys.RUNTIME_REVISION,
                QuickJsRuntimeContract.RUNTIME_REVISION,
            )
            putString(QuickJsPluginCapabilityKeys.SOURCE_TRANSPORT, "parcel-file-descriptor")
            putString(QuickJsPluginCapabilityKeys.EXECUTION_MODEL, "isolated-process-single-source")
            putBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_MODULES, true)
            putBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_CANCELLATION, true)
            putBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_STREAMING_OUTPUT, true)
            putBoolean(QuickJsPluginCapabilityKeys.SUPPORTS_MEMORY_LIMITS, true)
        }
    }
}
