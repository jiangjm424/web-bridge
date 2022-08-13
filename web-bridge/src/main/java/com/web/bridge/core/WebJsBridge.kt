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
                        agentWeb.jsAccessEntrace?.quickCallJs(
                            "bridge.onEvent",
                            "orientationChanged",
                            "{\"orientation\":\"PORTRAIT\"}"
                        )
                        orientationFlag = 1
                    }
                } else if (rotation in 81..99) {
                    if (orientationFlag != 4) {
                        Log.d(TAG, "  4 for reverse landscape:$rotation");
                        agentWeb.jsAccessEntrace?.quickCallJs(
                            "bridge.onEvent",
                            "orientationChanged",
                            "{\"orientation\":\"LANDSCAPE\"}"
                        )
                        orientationFlag = 4
                    }

                } else if (rotation in 171..189) {
                    if (orientationFlag != 3) {
                        Log.d(TAG, "3 for reverse portrait : $rotation");
                        agentWeb.jsAccessEntrace?.quickCallJs(
                            "bridge.onEvent",
                            "orientationChanged",
                            "{\"orientation\":\"PORTRAIT\"}"
                        )
                        orientationFlag = 3
                    }
                } else if (rotation in 261..279) {
                    if (orientationFlag != 2) {
                        Log.d(TAG, "2 for normal landscape : $rotation");
                        agentWeb.jsAccessEntrace?.quickCallJs(
                            "bridge.onEvent",
                            "orientationChanged",
                            "{\"orientation\":\"LANDSCAPE\"}"
                        )
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
                plugins[CLAZZ_UNKNOWN_PLUGIN].also {
                    it?.errMsg = clazz
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

    //check 应用打开的网页是指定的。以qq为例 ，后面再换
    //注意： 该方式只能检查首次打开h5时传入进来的url,如果是通过url内部的超链接跳转出去后，此时检测时仍使用首次打开的地址
    //eg: 首次打开fcb.bao.agency时，在h5内跳转到baidu.com。
    //那么如果baidu.com中也调用了postMessage的方法，那么他是能够调用成功的
    //todo 后续再考虑这个安全规则
    private fun safeCheck(): Boolean {
//        val url = URL(currentUrl)
//        val isSafePage = url.protocol == "https" && url.host.endsWith("qq.com")
//        if (!isSafePage) {
//            Log.e(TAG, "current url is not special page, check failed, current url $currentUrl")
//        }
        //todo 暂时放开，后面再定规则
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
