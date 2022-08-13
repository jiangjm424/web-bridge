package com.web.bridge.resp

import com.google.gson.Gson

object JsApiResult {
    private val gson = Gson()
    fun <T> success(d:T?): String {
        return d.toJsCommResp(gson, JsErrCode.ERR_OK, "Ok")
    }
    fun fail(ret:Int, errMsg:String): String {
        return "".toJsCommResp(gson, ret, errMsg)
    }
}
