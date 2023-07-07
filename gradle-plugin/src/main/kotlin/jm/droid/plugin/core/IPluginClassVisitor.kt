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

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class IPluginClassVisitor(classVisitor: ClassVisitor?) : ClassVisitor(C.V_ASM, classVisitor) {
    private lateinit var ownerClass: String
    private lateinit var superClass: String
    private val jsMethodList = mutableListOf<JsMethodInfo>()
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?,
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
//        println("--------**** class ****--------- e")
        println("The plugin:$name will be process")
//        println("superName:$superName")
//        println("interfaces:$interfaces")
//        println("signature:$signature")
        ownerClass = name!!
        superClass = superName!!
    }

    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?,
    ): MethodVisitor? {
//        println("-------- method ---------")
//        println("name:$name")
//        println("access:$access")
//        println("descriptor:$descriptor")
//        println("signature:$signature")
        if (name == C.FUN_DISPATCHJSEVENT_NAME && descriptor == C.FUN_DISPATCHJSEVENT_DESCRIPTOR) {
            println("override fun $name($descriptor) in $ownerClass will be gen by webbridge-gradle-plugin")
            return null
        }
        if (access.and(C.ACC_PUBLIC_FINAL) == C.ACC_PUBLIC_FINAL &&
            (
                descriptor.startsWith(C.FUN_DISPATCHJSEVENT_ONE_PARAM) ||
                    descriptor.startsWith(C.FUN_DISPATCHJSEVENT_THREE_PARAM)
                )
        ) {
            println("found method：$name$descriptor")
            jsMethodList.add(JsMethodInfo(name, descriptor))
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

    override fun visitEnd() {
        println("In $ownerClass found methods size:${jsMethodList.size}")
        jsMethodList.forEach {
            it.check()
        }
        jsMethodList.sort()
        val mv = super.visitMethod(
            Opcodes.ACC_PUBLIC,
            C.FUN_DISPATCHJSEVENT_NAME,
            C.FUN_DISPATCHJSEVENT_DESCRIPTOR,
            null,
            null,
        )
        genDispatchJsEventMethodWithMethods(mv, jsMethodList)
        super.visitEnd()
    }

    private fun genDispatchJsEventMethodWithMethods(
        mv: MethodVisitor,
        methods: List<JsMethodInfo>,
    ) {
        mv.visitCode()
        when (methods.size) {
            0 -> {
                DispatchJsEventTool.noMethod(ownerClass, superClass, mv)
            }

            1 -> {
                DispatchJsEventTool.oneMethod(ownerClass, superClass, mv, methods.first())
            }

            2 -> {
                DispatchJsEventTool.twoMethod(ownerClass, superClass, mv, methods)
            }

            else -> {
                DispatchJsEventTool.threeOrMoreMethod(ownerClass, superClass, mv, methods)
            }
        }
        mv.visitEnd()
    }
}
