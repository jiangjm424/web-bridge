/*
 * Copyright 2023 The Jmdroid Project
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

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.just.agentweb.WebViewClient

class DefaultWebViewClient(
    private val webAbilitiesClient: WebAbilitiesClient?,
    private val urlChangeCallback: ((pageUrl: String) -> Unit),
    private val onRenderProcessGone: OnRenderProcessGone?,
) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        val ff = favicon ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        super.onPageStarted(view, url, ff)
        val u = url ?: ""
        urlChangeCallback(u)
        webAbilitiesClient?.onPageStart(u)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webAbilitiesClient?.onPageFinish(url ?: "")
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?,
    ): Boolean {
        if (request?.url != null && webAbilitiesClient?.onOverrideUrlLoading(
                request.url,
                true,
            ) == true
        ) {
            return true
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        webAbilitiesClient?.onReceivedSslError(view, handler, error) ?: super.onReceivedSslError(
            view,
            handler,
            error,
        )
    }

    override fun onRenderProcessGone(
        view: WebView?,
        detail: RenderProcessGoneDetail?,
    ): Boolean {
        return onRenderProcessGone?.invoke(view, detail) ?: super.onRenderProcessGone(view, detail)
    }
}
typealias OnRenderProcessGone = (
    view: WebView?,
    detail: RenderProcessGoneDetail?,
) -> Boolean
