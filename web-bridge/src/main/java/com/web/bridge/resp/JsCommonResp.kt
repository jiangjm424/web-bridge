package com.web.bridge.resp

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class JsCommonResp<T>(
    @SerializedName(value = "ret")
    var ret: Int,
    @SerializedName(value = "errMsg")
    var errMsg: String,
    @SerializedName(value = "data")
    var data: T?
)

fun <T> T?.toJsCommResp(gson: Gson, ret: Int = 0, errMsg: String = "Ok"): String {
    val commonResp = JsCommonResp(ret, errMsg, this)
    return gson.toJson(commonResp)
}
