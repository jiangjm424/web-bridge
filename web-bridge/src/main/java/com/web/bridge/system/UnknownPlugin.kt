package com.web.bridge.system

import androidx.appcompat.app.AppCompatActivity
import com.web.bridge.iplugin.AbsPlugin
import com.web.bridge.resp.JsApiResult
import com.web.bridge.resp.JsErrCode

class UnknownPlugin(activity: AppCompatActivity) : AbsPlugin(activity) {
    override fun dispatchJsEvent(method: String, params: String?): String {
        return JsApiResult.fail(JsErrCode.ERR_NO_PLUGIN, errMsg ?: "unknown")
    }
}
