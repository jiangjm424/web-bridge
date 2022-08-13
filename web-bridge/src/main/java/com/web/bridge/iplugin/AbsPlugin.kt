package com.web.bridge.iplugin

import androidx.appcompat.app.AppCompatActivity

abstract class AbsPlugin constructor(protected val activity:AppCompatActivity): JsInvokeBridge {
    var errMsg:String?=null
}
