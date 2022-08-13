package com.web.bridge.core

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import com.just.agentweb.LollipopFixedWebView
import java.lang.Exception

internal object WebViewCacheManager {
    private const val TAG = "WebViewCacheManager"
    private val cache: MutableList<WebView> = mutableListOf()

    private fun crete(context: Context): LollipopFixedWebView {
        return LollipopFixedWebView(context)
    }

    @JvmStatic
    fun prepare(context: Context) {
        if (cache.isEmpty()) {
            Looper.myQueue().addIdleHandler {
                false
            }
        }
    }

    @JvmStatic
    fun obtain(context: Context): WebView {
        if (cache.isEmpty()) {
            cache.add(crete(MutableContextWrapper(context)))
        }
        val w = cache.removeFirst()
        val contextWrapper = w.context as MutableContextWrapper
        contextWrapper.baseContext = context
        w.clearHistory()
        w.resumeTimers()
        Log.i(TAG,"cache webview: $w")
        return w
    }

    @JvmStatic
    fun recycle(webView: WebView) {
        try {
            webView.stopLoading()
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView.clearHistory()
            webView.pauseTimers()
            webView.clearFormData()
            webView.removeJavascriptInterface("Native")
            val p = webView.parent as? ViewGroup
            p?.removeView(webView)
        } catch (e: Exception) {

        } finally {
            if (!cache.contains(webView)) {
                cache.add(webView)
            }
        }
    }

    @JvmStatic
    fun destroy() {
        try {
            cache.forEach {
                it.removeAllViews()
                it.destroy()
            }
        } catch (e: Exception) {

        } finally {
            cache.clear()
        }
    }
}
