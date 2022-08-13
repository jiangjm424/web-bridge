package com.grank.db.demo.plugins

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.web.bridge.iplugin.AbsPlugin
import com.web.bridge.resp.JsApiResult

class TestPlugin3(activity: AppCompatActivity) : AbsPlugin(activity) {
    companion object {
        private const val TAG = "TestPlugin3"
    }

    override fun dispatchJsEvent(method: String, params: String?): String {
        Log.i(TAG, "method:$method ,  param $params aa $activity")
        return JsApiResult.success("plugin 3 sucess")
    }
}
