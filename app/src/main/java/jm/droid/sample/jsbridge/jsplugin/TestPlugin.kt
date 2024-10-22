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
import jm.droid.lib.bridge.iplugin.AbsJsPlugin
import jm.droid.lib.bridge.resp.JsApiResult
import jm.droid.lib.bridge.resp.JsCommonReq

class TestPlugin(activity: AppCompatActivity, jsApiProxy: JsApiProxy?) :
    AbsJsPlugin(activity, jsApiProxy) {

    override fun onConfig(methods: MutableMap<String, (JsCommonReq) -> String>) {
        methods["one"] = put@{ req: JsCommonReq ->
            Log.i("Plugin", "method one 2")
            notifyJs("callByAndroid", JsApiResult.success(0, null, "fdasfa"))
            return@put JsApiResult.success(0, reqId = req.reqId, "on")
        }
        methods.put("two", two)
    }

    private val two = two@{ req: JsCommonReq ->
        return@two ""
    }
}
