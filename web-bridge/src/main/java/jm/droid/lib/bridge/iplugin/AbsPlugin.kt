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

package jm.droid.lib.bridge.iplugin

import androidx.appcompat.app.AppCompatActivity
import jm.droid.lib.bridge.core.JsApiProxy
import jm.droid.lib.bridge.resp.JsApiResult
import jm.droid.lib.bridge.resp.JsErrCode

/**
 * 可以使用插件来自动生成规范的 dispatchJsEvent 方法，插件使用方式如下：
 *root/build.gradle.kts
 buildscript {
 ......
 dependencies {
 classpath("io.github.jiangjm424:jsbridge-gradle-plugin:1.0.0-SNAPSHOT")
 }
 }
 *app/lib下的build.gradle.kts
 plugins {
 ......
 id("jm.droid.plugin.jsbridge")
 }

 之后插件会收集的方法：一个参数或者三个参数的public final，插件处理方法的原则是：
 1、方法没有返回值，则返回值ret=ERR_ASYNC，并且用户需要自己去回调js方法（如果需要）
 2、方法有返回值，同步执行后返回对应的json串
 建议按以下三种方法
 1、 fun(param:String):T
 只有一个参数时必须带返回值，这样可以直接同步返回到js
 2、 fun(param:String,reqId:String?,callback:String?)
 三个参数时，插件默认用户主动回调了，
 3、 fun(param:String,reqId:String?,callback:String?)：T
 三个参数时，插件默认用户主动回调了，

 如果需要回调js， 本库提供了一个方便的接口供子类调用：notifyJsCallback(callback: String, resp: String?)
 */
abstract class AbsPlugin constructor(
    protected val activity: AppCompatActivity,
    protected val jsApiProxy: JsApiProxy?,
) : JsInvokeBridge {
    var errMsg: String? = null

    companion object {
        private val empty = JsApiResult.success(ret = JsErrCode.ERR_OK, d = "", reqId = null)
    }

    override fun dispatchJsEvent(
        method: String,
        reqId: String?,
        params: String?,
        callback: String?,
    ): String {
        return JsApiResult.fail(JsErrCode.ERR_NO_METHOD, reqId, "no method found")
    }

    protected fun notifyJsCallback(callback: String, resp: String?) {
        jsApiProxy?.callJs(callback, resp ?: empty)
    }

    fun recycle() {
        onRecycle()
    }

    protected open fun onRecycle() {}
}
