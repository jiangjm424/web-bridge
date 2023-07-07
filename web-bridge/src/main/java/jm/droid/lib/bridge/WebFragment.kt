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

package jm.droid.lib.bridge

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.just.agentweb.AgentWeb
import jm.droid.lib.bridge.core.JsObjectCompact
import jm.droid.lib.bridge.core.OnRenderProcessGone
import jm.droid.lib.bridge.core.WebAbilitiesClient
import jm.droid.lib.bridge.core.WebDarkModeSettings
import jm.droid.lib.bridge.core.WebJsBridge
import jm.droid.lib.bridge.core.WebUtils
import jm.droid.lib.bridge.databinding.LayoutWebFragmentBinding

class WebFragment : Fragment() {
    companion object {
        private const val TAG = "WebFragment"
        private const val ARGUMENT_KEY_WEB_URL = "url_key"
        private const val ARGUMENT_KEY_WEB_INDICATOR = "indicator_key"
        private const val ARGUMENT_KEY_ERROR_LAYOUT = "error_layout_key"
        private const val ARGUMENT_KEY_DARK_MODE = "dark_mode_key"

        fun newInstance(
            url: String,
            @WebDarkModeSettings.DarkMode darkMode: Int = WebDarkModeSettings.DARK_AUTO,
            @LayoutRes errorLayout: Int = R.layout.layout_error,
            @ColorRes indicator: Int = R.color.web_indicator,
        ): WebFragment {
            val f = WebFragment()
            f.arguments = bundleOf(
                ARGUMENT_KEY_WEB_URL to url,
                ARGUMENT_KEY_DARK_MODE to darkMode,
                ARGUMENT_KEY_WEB_INDICATOR to indicator,
                ARGUMENT_KEY_ERROR_LAYOUT to errorLayout,
            )
            return f
        }
    }

    private val targetUrl by lazy {
        arguments?.getString(ARGUMENT_KEY_WEB_URL) ?: "http://m.jd.com"
    }
    private val webIndicator by lazy {
        arguments?.getInt(ARGUMENT_KEY_WEB_INDICATOR, R.color.web_indicator) ?: R.color.web_indicator
    }
    private val errorLayout by lazy {
        arguments?.getInt(ARGUMENT_KEY_ERROR_LAYOUT, R.layout.layout_error) ?: R.layout.layout_error
    }
    private val darkMode by lazy {
        arguments?.getInt(ARGUMENT_KEY_DARK_MODE) ?: WebDarkModeSettings.DARK_AUTO
    }

    private var webAbilitiesFactory: WebAbilitiesClient.Factory? = null

    private var jsObjectCompact: JsObjectCompact? = null

    private val onRenderProcessGone: OnRenderProcessGone = { view, detail ->
        if (detail?.didCrash() == false) { // 被系统杀掉的时候可以重新生成webview，防止应用闪退
            binding.rootContainer.removeAllViews()
            view?.removeJavascriptInterface(WebJsBridge.ID_JS_OBJ)
            mAgentWeb?.webLifeCycle?.let {
                onPause()
                onDestroy()
            }
            mAgentWeb = null
            mAgentWeb = createWebAgent(
                LayoutInflater.from(requireContext()),
                binding.rootContainer,
            )?.apply {
                initJSBridgeObject(this@apply)
            }
            true
        } else {
            false
        }
    }
    private var mAgentWeb: AgentWeb? = null
    private var webJsBridge: WebJsBridge? = null

    private var _binding: LayoutWebFragmentBinding? = null
    private val binding: LayoutWebFragmentBinding
        get() = _binding!!

    private val backPressedCallback by lazy {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                mAgentWeb?.back()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        webAbilitiesFactory = context as? WebAbilitiesClient.Factory
        jsObjectCompact = context as? JsObjectCompact
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutWebFragmentBinding.inflate(inflater, container, false)
        mAgentWeb = createWebAgent(inflater, binding.rootContainer)?.apply {
            initJSBridgeObject(this@apply)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback,
        )
        return binding.root
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAgentWeb?.webCreator?.webView?.removeJavascriptInterface(WebJsBridge.ID_JS_OBJ)
        mAgentWeb?.webLifeCycle?.onDestroy()
//        mAgentWeb?.webCreator?.webView?.let {
//            WebViewCacheManager.recycle(it)
//        }
        _binding = null
    }

    @JvmOverloads
    fun invokeJs(method: String, vararg params: String, callback: ((String?) -> Unit)? = null) {
        if (callback == null) {
            mAgentWeb?.jsAccessEntrace?.quickCallJs(method, *params)
        } else {
            val aaa: ValueCallback<String> = ValueCallback<String> {
                callback.invoke(it)
            }
            mAgentWeb?.jsAccessEntrace?.quickCallJs(method, aaa, *params)
        }
    }

    private fun createWebAgent(inflater: LayoutInflater, rootContainer: ViewGroup): AgentWeb? {
        return WebUtils.buildAgentWeb(
            activity = requireActivity() as AppCompatActivity,
            container = rootContainer,
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
            indicatorColor = ContextCompat.getColor(requireContext(), webIndicator),
            darkMode = darkMode,
            factory = webAbilitiesFactory,
            errorLayoutRes = errorLayout,
            targetUrl = targetUrl,
            pageUrlChangeCallback = {
                mAgentWeb?.let {
                    backPressedCallback.isEnabled = it.webCreator.webView.canGoBack()
                }
            },
            onRenderProcessGone = onRenderProcessGone,
        )
    }

    private fun initJSBridgeObject(agent: AgentWeb) {
        webJsBridge = WebJsBridge(
            activity = requireActivity() as AppCompatActivity,
            lifecycle = viewLifecycleOwner.lifecycle,
            agentWeb = agent,
            fullscreenHandler = {
                Log.d(TAG, "fullscreen.")
            },
            lockScreenOrientationHandler = { partial ->
                activity?.requestedOrientation = if (partial) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            },
        )
        jsObjectCompact?.compactJsObject()?.forEach { (key, js) ->
            agent.jsInterfaceHolder?.addJavaObject(key, js)
        }
        agent.jsInterfaceHolder?.addJavaObject(
            WebJsBridge.ID_JS_OBJ,
            webJsBridge,
        ) // js中可以通过WebJsBridge.ID_JS_OBJ调用native代码
    }
}
