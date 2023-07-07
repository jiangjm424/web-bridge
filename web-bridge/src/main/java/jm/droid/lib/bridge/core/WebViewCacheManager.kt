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
        Log.i(TAG, "cache webview: $w")
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
