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

object JsErrCode {
    const val ERR_OK = 0
    const val ERR_ASYNC = 1

    const val ERR_NO_PLUGIN = -1000
    const val ERR_NO_METHOD = -1001
    const val ERR_NO_PERMISSION = -1002
}
