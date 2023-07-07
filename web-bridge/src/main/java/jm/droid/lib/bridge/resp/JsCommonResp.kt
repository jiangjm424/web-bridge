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

package jm.droid.lib.bridge.resp

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

data class JsCommonResp<T>(
    @Json(name = "ret")
    var ret: Int,
    @Json(name = "errMsg")
    var errMsg: String,
    @Json(name = "respId")
    var respId: String?,
    @Json(name = "data")
    var data: T?,
)

inline fun <reified T> T?.toJsCommResp(
    gson: Moshi,
    reqId: String?,
    ret: Int = 0,
    errMsg: String = "Ok",
): String {
    val commonResp = JsCommonResp(ret, errMsg, reqId, this)
    val type = Types.newParameterizedType(JsCommonResp::class.java, T::class.java)
    val a = gson.adapter<JsCommonResp<T>>(type)
    return a.toJson(commonResp)
}
