package com.web.bridge.core

import android.hardware.SensorManager
import android.util.Log
import android.view.OrientationEventListener
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.just.agentweb.AgentWeb
import com.web.bridge.iplugin.AbsPlugin
import com.web.bridge.system.UnknownPlugin
import kotlinx.coroutines.CoroutineScope
import java.net.URL

class WebJsBridge(
    private val activity: AppCompatActivity,
    lifecycle: Lifecycle,
    private val agentWeb: AgentWeb,
    private val fullscreenHandler: ((fullscreen: Boolean) -> Unit),
    private val lockScreenOrientationHandler: ((portrait: Boolean) -> Unit)
) {

    companion object {
        private const val TAG = "WebJsBridge"
        const val ID_JS_OBJ = "Native"
        private const val CLAZZ_UNKNOWN_PLUGIN = "com.web.bridge.system.UnknownPlugin"
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
            SensorManager.SENSOR_DELAY_NORMAL
        ) {
            override fun onOrientationChanged(rotation: Int) {
                //判断四个方向 //1表示正竖屏，2表示正横屏，3表示反竖屏，4表示反横屏
                if (rotation == -1) {
                    Log.d(TAG, " phone no rotation : $rotation")
                } else if (rotation < 10 || rotation > 350) {
                    if (orientationFlag != 1) {
                        Log.d(TAG, " 1 for normal portrait : $rotation");
                        orientationFlag = 1
                    }
                } else if (rotation in 81..99) {
                    if (orientationFlag != 4) {
                        Log.d(TAG, "  4 for reverse landscape:$rotation");
                        orientationFlag = 4
                    }

                } else if (rotation in 171..189) {
                    if (orientationFlag != 3) {
                        Log.d(TAG, "3 for reverse portrait : $rotation");
                        orientationFlag = 3
                    }
                } else if (rotation in 261..279) {
                    if (orientationFlag != 2) {
                        Log.d(TAG, "2 for normal landscape : $rotation");
                        orientationFlag = 2
                    }

                }
            }
        }
        listener
    }

    private val plugins = mutableMapOf<String, AbsPlugin>().apply {
        put(CLAZZ_UNKNOWN_PLUGIN, UnknownPlugin(activity))
    }

    private fun getPlugin(clazz: String): AbsPlugin? {
        var plugin = plugins[clazz]
        if (plugin == null) {
            plugin = try {
                val pluginClazz = Class.forName(clazz)
                val constructor = pluginClazz.getConstructor(AppCompatActivity::class.java)
                val obj = constructor.newInstance(activity) as AbsPlugin
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
    fun nativeMethod(clazz: String, method: String, params: String?, callback: String?) {
        Log.i(TAG, "clazz: $clazz, method:$method, params: $params, callback: $callback")
        //h5网页合法性检查。如果不合规则的h5页面不允许调用native方法
        if (!safeCheck()) return
        val resp = getPlugin(clazz)?.dispatchJsEvent(method, params)
        callback?.let {
            agentWeb.jsAccessEntrace.quickCallJs(it, resp)
        }
        Log.i(TAG, "resp:$resp")
    }

    //todo 后续再考虑这个安全规则
    private fun safeCheck(): Boolean {
        return true
    }

    private fun destroy() {
        if (mOrientationListener.isInitialized()) {
            val listener = mOrientationListener.value
            listener.disable()
        }
        plugins.clear()
    }
}
