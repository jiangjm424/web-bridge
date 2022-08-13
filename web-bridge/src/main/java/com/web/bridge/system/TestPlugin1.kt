package com.web.bridge.system

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.web.bridge.resp.Plugin1Resp
import com.web.bridge.iplugin.AbsPlugin
import com.web.bridge.resp.JsApiResult

class TestPlugin1(activity: AppCompatActivity) : AbsPlugin(activity) {
    companion object {
        private const val TAG = "TestPlugin1"
    }
    private var  aa :String = "defalut"
    override fun dispatchJsEvent(method: String, params: String?): String {
        Log.i(TAG,"method:$method ,  param $params aa $activity")
        val data = Plugin1Resp("my id", "my jiang name")
        return JsApiResult.success(data)
    }
}
