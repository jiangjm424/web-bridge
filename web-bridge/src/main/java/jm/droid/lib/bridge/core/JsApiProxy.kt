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

package jm.droid.lib.bridge.core

import android.webkit.ValueCallback
import com.just.agentweb.AgentWeb

class JsApiProxy internal constructor(private val agentWeb: AgentWeb) {
    @JvmOverloads
    fun callJs(method: String, vararg params: String, callback: ((String?) -> Unit)? = null) {
        if (callback == null) {
            agentWeb.jsAccessEntrace?.quickCallJs(method, *params)
        } else {
            val aaa: ValueCallback<String> = ValueCallback<String> {
                callback.invoke(it)
            }
            agentWeb.jsAccessEntrace?.quickCallJs(method, aaa, *params)
        }
    }
}
