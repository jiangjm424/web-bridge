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

import org.objectweb.asm.Opcodes

object C {
    const val V_ASM = Opcodes.ASM7
    const val ACC_PUBLIC_FINAL = Opcodes.ACC_FINAL.or(Opcodes.ACC_PUBLIC)
    const val FUN_DISPATCHJSEVENT_NAME = "dispatchJsEvent"
    const val FUN_DISPATCHJSEVENT_DESCRIPTOR =
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    const val FUN_DISPATCHJSEVENT_ONE_PARAM = "(Ljava/lang/String;)"
    const val FUN_DISPATCHJSEVENT_THREE_PARAM =
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)"
    const val AbsPlugin = "jm.droid.lib.bridge.iplugin.AbsPlugin"
}
