package com.web.bridge.core

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import com.just.agentweb.IWebLayout
import com.web.bridge.databinding.ViewWebContainerBinding

class BridgeWebLayout(inflater: LayoutInflater) : IWebLayout<WebView, ViewGroup> {
    private val b = ViewWebContainerBinding.inflate(inflater)
    private val w = WebViewCacheManager.obtain(b.webContainer.context)
    override fun getLayout(): ViewGroup {
        b.webContainer.addView(w)
        return b.root
    }

    override fun getWebView(): WebView {
        return w
    }
}
