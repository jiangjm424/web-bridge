package com.web.bridge.iplugin

interface JsInvokeBridge {
    fun dispatchJsEvent(method:String, params:String?):String
}
