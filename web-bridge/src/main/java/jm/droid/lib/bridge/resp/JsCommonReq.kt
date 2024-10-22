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

data class JsCommonReq(
    @Json(name = "callbackId")
    var callbackId: String?,
    @Json(name = "reqId")
    var reqId: String?,
    @Json(name = "data")
    var data: String?, // h5传参数，可以为空，也可以为json串，也可以直接是字符串，由实际业务确认即可，此处框架直接透传
)
