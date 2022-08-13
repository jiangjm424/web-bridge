package com.web.bridge.core

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.just.agentweb.*
import com.web.bridge.R
import java.lang.ref.WeakReference

object WebUtils {

    fun buildAgentWeb(
        activity: AppCompatActivity,
        container: ViewGroup,
        layoutParams: ViewGroup.LayoutParams,
        titleReceived: ((title: String) -> Unit)?,
        defaultTitle: String = "",
        targetUrl: String,
        inflater: LayoutInflater,
        pageUrlChangeCallback: (pageUrl: String) -> Unit
    ): AgentWeb {
        val agentWebSettings = createAgentWebSettings(targetUrl, activity)
        return AgentWeb.with(activity)
            .setAgentWebParent(
                container,
                layoutParams
            )
            .useDefaultIndicator(Color.parseColor("#FF539DFF"), 2)
            .setAgentWebWebSettings(agentWebSettings)//设置 IAgentWebSettings。
            .setWebViewClient(BridgeWebClient(pageUrlChangeCallback))
            .setWebChromeClient(BridgeWebChromeClient(titleReceived, defaultTitle))
            .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//            .setWebLayout(BridgeWebLayout(inflater))
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
            .interceptUnkownUrl() //拦截找不到相关页面的Scheme
            .createAgentWeb()
            .ready().go(targetUrl)
    }

    private fun createAgentWebSettings(targetUrl: String, activity: Activity): AbsAgentWebSettings {
        val weakActivityRef = WeakReference(activity)
        return object : AbsAgentWebSettings() {
            override fun toSetting(webView: WebView?): IAgentWebSettings<*> {
                super.toSetting(webView)
                val userAgent = webSettings.userAgentString
                val ua = userAgent.plus(" terminal/saas")
//                    .plus(" versionCode/${webView?.context?.getVersionCode()}")
//                    .plus(" versionName/${webView?.context?.getVersionName()}")
                webSettings.userAgentString = ua
                return this
            }

            override fun bindAgentWebSupport(agentWeb: AgentWeb) {
                this.setDownloader(agentWeb.webCreator?.webView) { _, _, _, _, _ ->
                    weakActivityRef.get()?.run {
                        val uri = Uri.parse(targetUrl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                }
            }
        }
    }


    class BridgeWebChromeClient(
        private val titleBar: ((title: String) -> Unit)?,
        private val defaultTitle: String
    ) : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            if (title.isEmpty() || view.url?.startsWith("data:") == true) {
                titleBar?.invoke(defaultTitle)
            } else {
                titleBar?.invoke(title)
            }
        }

        override fun getDefaultVideoPoster(): Bitmap? {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
    }

    // TODO 这里为了处理Kotlin Bug https://blog.csdn.net/binglumeng/article/details/86228344
    class BridgeWebClient(private val urlChangeCallback: (pageUrl: String) -> Unit) :
        WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            val ff = favicon ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            super.onPageStarted(view, url, ff)
            urlChangeCallback.invoke(url ?: "")
        }

    }


}
