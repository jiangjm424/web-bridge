package com.web.bridge

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.just.agentweb.AgentWeb
import com.web.bridge.core.WebJsBridge
import com.web.bridge.core.WebTitleCallback
import com.web.bridge.core.WebUtils
import com.web.bridge.databinding.LayoutWebFragmentBinding

class WebFragment : Fragment() {
    companion object {
        private const val TAG = "WebFragment"
        const val ARGUMENT_KEY_WEB_URL = "url_key"
        fun newInstance(url: String): WebFragment {
            val f = WebFragment()
            f.arguments = bundleOf(
                ARGUMENT_KEY_WEB_URL to url
            )
            return f
        }
    }

    private val targetUrl by lazy {
        arguments?.getString(ARGUMENT_KEY_WEB_URL) ?: "http://m.jd.com"
    }
    private var webTitleCallback: WebTitleCallback? = null
    private var mAgentWeb: AgentWeb? = null
    private var webJsBridge: WebJsBridge? = null

    private var _binding: LayoutWebFragmentBinding? = null
    private val binding: LayoutWebFragmentBinding
        get() = _binding!!

    private val backPressedCallback by lazy {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
//                mAgentWeb?.
                mAgentWeb?.back()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        webTitleCallback = context as? WebTitleCallback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutWebFragmentBinding.inflate(inflater, container, false)
        mAgentWeb = createWebAgent(inflater, binding.rootContainer).apply {
            initJSBridgeObject(this@apply)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
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

    private fun createWebAgent(inflater: LayoutInflater, rootContainer: ViewGroup): AgentWeb {
        return WebUtils.buildAgentWeb(
            activity = requireActivity() as AppCompatActivity,
            container = rootContainer,
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ),
            titleReceived = { title: String -> webTitleCallback?.onWebTitle(title) },
            targetUrl = targetUrl,
            inflater = inflater,
            pageUrlChangeCallback = {
                mAgentWeb?.let {
                    backPressedCallback.isEnabled = it.webCreator.webView.canGoBack()
                }
            }
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
            }
        )
        agent.jsInterfaceHolder?.addJavaObject(
            WebJsBridge.ID_JS_OBJ,
            webJsBridge
        ) //js中可以通过WebJsBridge.ID_JS_OBJ调用native代码
    }
}
