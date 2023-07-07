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

package jm.droid.lib.bridge.system

import androidx.appcompat.app.AppCompatActivity
import jm.droid.lib.bridge.core.JsApiProxy
import jm.droid.lib.bridge.iplugin.AbsPlugin
import jm.droid.lib.bridge.resp.JsApiResult
import jm.droid.lib.bridge.resp.JsErrCode

class UnknownPlugin(activity: AppCompatActivity, jsApiProxy: JsApiProxy) : AbsPlugin(
    activity,
    jsApiProxy,
) {
    override fun dispatchJsEvent(
        method: String,
        reqId: String?,
        params: String?,
        callback: String?,
    ): String {
        return JsApiResult.fail(JsErrCode.ERR_NO_PLUGIN, reqId, errMsg ?: "unknown")
    }
}
