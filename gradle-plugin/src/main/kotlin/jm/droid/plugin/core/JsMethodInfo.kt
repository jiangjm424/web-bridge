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

package jm.droid.plugin.core

import org.gradle.api.GradleException

data class JsMethodInfo(
    val name: String,
    val descriptor: String,
) : Comparable<JsMethodInfo> {
    val hashCode: Int = name.hashCode()
    val paramNum: Int = if (descriptor.startsWith(C.FUN_DISPATCHJSEVENT_ONE_PARAM)) 1 else 3
    val returnVoidValue: Boolean = descriptor.endsWith(")V")
    override fun compareTo(other: JsMethodInfo): Int {
        return hashCode.compareTo(other.hashCode)
    }

    fun check() {
        if (returnVoidValue && paramNum != 3) {
            throw GradleException("fun $name$descriptor may use async callback but with no reqId,callback")
        }
    }
}
