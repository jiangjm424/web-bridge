/*
 * Copyright 2023 The Jmdroid Project
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

package jm.droid.sample.jsbridge.jsplugin

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jm.droid.lib.bridge.core.JsApiProxy
import jm.droid.lib.bridge.iplugin.AbsPlugin
import jm.droid.lib.bridge.resp.JsApiResult

class TestPlugin(activity: AppCompatActivity, jsApiProxy: JsApiProxy?) :
    AbsPlugin(activity, jsApiProxy) {

    fun one(params: String, reqId: String?, callback: String?) {
        Log.i("Plugin", "method one")
        callback?.let {
            notifyJsCallback(callback, JsApiResult.success(reqId = reqId, d = "adfas"))
        }
    }

    fun two(params: String): String {
        return "two"
    }

    fun three(params: String, reqId: String?, callback: String?): String {
        val d = "data"
        callback?.let {
            notifyJsCallback(it, JsApiResult.success(reqId = reqId, d = d))
        }
        return "three"
    }
}
