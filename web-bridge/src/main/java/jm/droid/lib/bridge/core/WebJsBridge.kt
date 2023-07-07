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

import android.hardware.SensorManager
import android.util.Log
import android.view.OrientationEventListener
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.just.agentweb.AgentWeb
import jm.droid.lib.bridge.iplugin.AbsPlugin
import jm.droid.lib.bridge.resp.JsApiResult
import jm.droid.lib.bridge.resp.JsErrCode
import jm.droid.lib.bridge.system.UnknownPlugin

class WebJsBridge(
    private val activity: AppCompatActivity,
    lifecycle: Lifecycle,
    agentWeb: AgentWeb,
    private val fullscreenHandler: ((fullscreen: Boolean) -> Unit),
    private val lockScreenOrientationHandler: ((portrait: Boolean) -> Unit),
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

    private var orientationFlag = 1

    init {
        lifecycle.addObserver(lifecycleObserver)
    }

    private val mOrientationListener: Lazy<OrientationEventListener> = lazy {
        val listener = object : OrientationEventListener(
            activity.applicationContext,
            SensorManager.SENSOR_DELAY_NORMAL,
        ) {
            override fun onOrientationChanged(rotation: Int) {
                // 判断四个方向 //1表示正竖屏，2表示正横屏，3表示反竖屏，4表示反横屏
                if (rotation == -1) {
                    Log.d(TAG, " phone no rotation : $rotation")
                } else if (rotation < 10 || rotation > 350) {
                    if (orientationFlag != 1) {
                        Log.d(TAG, " 1 for normal portrait : $rotation")
                        orientationFlag = 1
                    }
                } else if (rotation in 81..99) {
                    if (orientationFlag != 4) {
                        Log.d(TAG, "  4 for reverse landscape:$rotation")
                        orientationFlag = 4
                    }
                } else if (rotation in 171..189) {
                    if (orientationFlag != 3) {
                        Log.d(TAG, "3 for reverse portrait : $rotation")
                        orientationFlag = 3
                    }
                } else if (rotation in 261..279) {
                    if (orientationFlag != 2) {
                        Log.d(TAG, "2 for normal landscape : $rotation")
                        orientationFlag = 2
                    }
                }
            }
        }
        listener
    }

    private val jsApiProxy = JsApiProxy(agentWeb)
    private val plugins = mutableMapOf<String, AbsPlugin>().apply {
        put(CLAZZ_UNKNOWN_PLUGIN, UnknownPlugin(activity, jsApiProxy))
    }

    private fun getPlugin(clazz: String): AbsPlugin? {
        var plugin = plugins[clazz]
        if (plugin == null) {
            plugin = try {
                val pluginClazz = Class.forName(clazz)
                val constructor = pluginClazz.getConstructor(
                    AppCompatActivity::class.java,
                    JsApiProxy::class.java,
                )
                val obj = constructor.newInstance(activity, jsApiProxy) as AbsPlugin
                plugins.putIfAbsent(clazz, obj)
                obj
            } catch (ex: ClassNotFoundException) {
                plugins[CLAZZ_UNKNOWN_PLUGIN]?.also {
                    it.errMsg = clazz
                }
            }
        }
        return plugin
    }

    @JavascriptInterface
    fun nativeMethod(clazz: String, method: String, reqId: String?, params: String?, callback: String?): String? {
        Log.v(TAG, "clazz: $clazz, method:$method, params: $params, callback: $callback")
        // h5网页合法性检查。如果不合规则的h5页面不允许调用native方法
        if (!safeCheck()) return JsApiResult.fail(JsErrCode.ERR_NO_PERMISSION, reqId, "no permission")
        val resp = getPlugin(clazz)?.dispatchJsEvent(method, reqId, params, callback)
        Log.v(TAG, "resp:$resp")
        return resp
    }

    // todo 后续再考虑这个安全规则
    private fun safeCheck(): Boolean {
        return true
    }

    private fun destroy() {
        if (mOrientationListener.isInitialized()) {
            val listener = mOrientationListener.value
            listener.disable()
        }
        plugins.forEach { (_, plugin) ->
            plugin.recycle()
        }
        plugins.clear()
    }
}
