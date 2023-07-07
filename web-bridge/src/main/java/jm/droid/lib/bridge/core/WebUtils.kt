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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.just.agentweb.AbsAgentWebSettings
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.IAgentWebSettings
import com.just.agentweb.R
import java.lang.ref.WeakReference

internal object WebUtils {
    fun buildAgentWeb(
        activity: AppCompatActivity,
        container: ViewGroup,
        layoutParams: ViewGroup.LayoutParams,
        @ColorInt indicatorColor: Int,
        factory: WebAbilitiesClient.Factory?,
        @WebDarkModeSettings.DarkMode darkMode: Int,
        @LayoutRes errorLayoutRes: Int,
        pageUrlChangeCallback: (pageUrl: String) -> Unit,
        onRenderProcessGone: OnRenderProcessGone?,
        targetUrl: String,
    ): AgentWeb {
        val agentWebSettings =
            createAgentWebSettings(factory?.webSettingsConfig(), targetUrl, activity, darkMode)
        val errorLayout = errorLayoutRes.takeIf { it > 0 } ?: R.layout.agentweb_error_page
        val agentBuilder = AgentWeb.with(activity).setAgentWebParent(container, layoutParams)
            .useDefaultIndicator(indicatorColor, 2)
            .setAgentWebWebSettings(agentWebSettings)
            .setMainFrameErrorView(errorLayout, -1)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .interceptUnkownUrl()
        // 设置web chrome client
        factory?.webChromeClient()?.let {
            agentBuilder.setWebChromeClient(it)
        } ?: kotlin.run {
            agentBuilder.setWebChromeClient(DefaultWebChromeClient(factory?.webAbilitiesClient()))
        }
        // 设置web view client
        factory?.webViewClient()?.let {
            agentBuilder.setWebViewClient(it)
        } ?: kotlin.run {
            agentBuilder.setWebViewClient(
                DefaultWebViewClient(
                    factory?.webAbilitiesClient(),
                    pageUrlChangeCallback,
                    onRenderProcessGone,
                ),
            )
        }
        // 设置url是否显示错误ui的回调
        factory?.mainFrameError().let {
            agentBuilder.setAgentWebUIController(DefaultAgentUiController(it))
        }
        return agentBuilder.createAgentWeb().ready().go(targetUrl)
    }

    private fun createAgentWebSettings(
        config: WebSettingsConfig?,
        targetUrl: String,
        activity: Activity,
        @WebDarkModeSettings.DarkMode mode: Int,
    ): AbsAgentWebSettings {
        val weakActivityRef = WeakReference(activity)
        return object : AbsAgentWebSettings() {
            override fun toSetting(webView: WebView?): IAgentWebSettings<*> {
                super.toSetting(webView)
                config?.onWebSettingsConfig(webSettings)
                val userAgent = webSettings.userAgentString
                val ua = userAgent
//                    .plus(";versionCode:${webView?.context?.getVersionCode()}")
//                    .plus(";versionName:${webView?.context?.getVersionName()}")
                webSettings.userAgentString = ua
                WebDarkModeSettings.configDarkSetting(webSettings, mode, webView?.context)
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
}
