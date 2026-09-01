package io.github.supermonster003.autojs6.plugin.quickjs.runtime

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.common.api.IPluginInfoProvider
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.quickjs.runtime.api.QuickJsRuntimeContract

class QuickJsPluginInfoService : Service() {
    override fun onBind(intent: Intent?): IBinder = binder

    private val binder = object : IPluginInfoProvider.Stub() {
        override fun getInfo(): PluginInfo {
            enforceCallingPermission(
                QuickJsRuntimeContract.PLUGIN_PERMISSION,
                "QuickJS plugin metadata requires the AutoJs6 plugin permission",
            )
            return applicationContext.quickJsPluginInfo()
        }
    }
}
