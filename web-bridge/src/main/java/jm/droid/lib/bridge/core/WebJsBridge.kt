/*
 * Copyright 2022 The Jmdroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jm.droid.lib.bridge.core

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.just.agentweb.AgentWeb
import jm.droid.lib.bridge.iplugin.AbsJsPlugin
import jm.droid.lib.bridge.resp.JsApiResult
import jm.droid.lib.bridge.resp.JsCommonReq
import jm.droid.lib.bridge.resp.JsErrCode
import jm.droid.lib.bridge.system.UnknownPlugin

class WebJsBridge(
    private val activity: AppCompatActivity,
    lifecycle: Lifecycle,
    agentWeb: AgentWeb,
) {

    companion object {
        private const val TAG = "WebJsBridge"
        const val ID_JS_OBJ = "Native"
        private const val CLAZZ_UNKNOWN_PLUGIN = "jm.droid.lib.bridge.UnknownPlugin"
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            destroy()
        }
    }

    init {
        lifecycle.addObserver(lifecycleObserver)
    }

    private val jsApiProxy = JsApiProxy(agentWeb)
    private val plugins = mutableMapOf<String, AbsJsPlugin>().apply {
        put(CLAZZ_UNKNOWN_PLUGIN, UnknownPlugin(activity, jsApiProxy))
    }

    @Synchronized
    private fun getPlugin(clazz: String): AbsJsPlugin? {
        var plugin = plugins[clazz]
        if (plugin == null) {
            plugin = try {
                val pluginClazz = Class.forName(clazz)
                val constructor = pluginClazz.getConstructor(
                    AppCompatActivity::class.java,
                    JsApiProxy::class.java,
                )
                val obj = constructor.newInstance(activity, jsApiProxy) as AbsJsPlugin
                obj.doConfig()
                plugins.putIfAbsent(clazz, obj)
                obj
            } catch (ex: ClassNotFoundException) {
                val obj = UnknownPlugin(activity, jsApiProxy).apply { errMsg = clazz }
                plugins.putIfAbsent(clazz, obj) // 防止不存在的类多次进行反射
                obj
            }
        }
        return plugin
    }

    @JavascriptInterface
    fun nativeMethod(
        clazz: String,
        method: String,
        reqId: String?,
        params: String?,
        callback: String?,
    ): String? {
        Log.v(TAG, "clazz: $clazz, method:$method, params: $params, callback: $callback")
        // h5网页合法性检查。如果不合规则的h5页面不允许调用native方法
        if (!safeCheck()) {
            return JsApiResult.fail(
                JsErrCode.ERR_NO_PERMISSION,
                reqId,
                "no permission",
            )
        }
        val req = JsCommonReq(callback, reqId, params)
        val resp = getPlugin(clazz)?.dispatchJsEvent(method, req)
        Log.v(TAG, "resp:$resp")
        return resp
    }

    // todo 后续再考虑这个安全规则
    private fun safeCheck(): Boolean {
        return true
    }

    private fun destroy() {
        plugins.forEach { (_, plugin) ->
            plugin.recycle()
        }
        plugins.clear()
    }
}
