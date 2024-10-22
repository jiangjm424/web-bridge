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

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsApiResult {
    val moshi: Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(Unit::class.java, UnitJsonAdapter())
            .build()

    @JvmOverloads
    inline fun <reified T> success(ret: Int = JsErrCode.ERR_OK, reqId: String?, d: T?): String {
        return d.toJsCommResp(moshi, reqId, ret, "Ok")
    }

    fun fail(ret: Int, reqId: String?, errMsg: String): String {
        return "".toJsCommResp(moshi, reqId, ret, errMsg)
    }

    inline fun <reified T> parse(json: String): JsCommonResp<T>? {
        val type = Types.newParameterizedType(JsCommonResp::class.java, T::class.java)
        val a = moshi.adapter<JsCommonResp<T>>(type)
        return a.fromJson(json)
    }
}
