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

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.AASTORE
import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ANEWARRAY
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.ASTORE
import org.objectweb.asm.Opcodes.CHECKCAST
import org.objectweb.asm.Opcodes.DUP
import org.objectweb.asm.Opcodes.GETSTATIC
import org.objectweb.asm.Opcodes.GOTO
import org.objectweb.asm.Opcodes.ICONST_0
import org.objectweb.asm.Opcodes.ICONST_1
import org.objectweb.asm.Opcodes.IFEQ
import org.objectweb.asm.Opcodes.IFNE
import org.objectweb.asm.Opcodes.ILOAD
import org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.Opcodes.ISTORE
import org.objectweb.asm.Opcodes.NEW
import org.objectweb.asm.Opcodes.NOP
import org.objectweb.asm.Type

object DispatchJsEventTool {
    private const val UNIT = "kotlin/Unit"
    private const val UNIT_DESC = "Lkotlin/Unit;"
    private const val INSTANCE = "INSTANCE"
    private const val JsApiResult = "jm/droid/lib/bridge/resp/JsApiResult"
    private const val JsApiResult_DESC = "Ljm/droid/lib/bridge/resp/JsApiResult;"
    private const val JsCommonResp = "jm/droid/lib/bridge/resp/JsCommonResp"
    private const val JsCommonResp_DESC = "Ljm/droid/lib/bridge/resp/JsCommonResp;"
    fun noMethod(clazz: String, superClass: String, methodVisitor: MethodVisitor) {
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 4)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            superClass,
            C.FUN_DISPATCHJSEVENT_NAME,
            C.FUN_DISPATCHJSEVENT_DESCRIPTOR,
            false,
        )
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(5, 5)
    }

    fun oneMethod(
        clazz: String,
        superClass: String,
        methodVisitor: MethodVisitor,
        method: JsMethodInfo,
    ) {
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitLdcInsn(method.name)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "areEqual",
            "(Ljava/lang/Object;Ljava/lang/Object;)Z",
            false,
        )
        val label0 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 3)
        if (method.paramNum == 3) {
            methodVisitor.visitVarInsn(ALOAD, 2)
            methodVisitor.visitVarInsn(ALOAD, 4)
        }
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            clazz,
            method.name,
            method.descriptor,
            false,
        )
        val label1 = Label()
        methodVisitor.visitJumpInsn(GOTO, label1)
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 4)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            superClass,
            C.FUN_DISPATCHJSEVENT_NAME,
            C.FUN_DISPATCHJSEVENT_DESCRIPTOR,
            false,
        )
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitLabel(label1)
        if (method.returnVoidValue) {
            methodVisitor.visitFieldInsn(GETSTATIC, UNIT, INSTANCE, UNIT_DESC)
        }
        methodVisitor.visitVarInsn(ASTORE, 5)
        methodVisitor.visitVarInsn(ALOAD, 5)
        methodVisitor.visitFieldInsn(GETSTATIC, UNIT, INSTANCE, UNIT_DESC)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "areEqual",
            "(Ljava/lang/Object;Ljava/lang/Object;)Z",
            false,
        )
        val label2 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label2)
        methodVisitor.visitInsn(ICONST_1)
        val label3 = Label()
        methodVisitor.visitJumpInsn(GOTO, label3)
        methodVisitor.visitLabel(label2)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLabel(label3)
        methodVisitor.visitVarInsn(ISTORE, 6)
        methodVisitor.visitFieldInsn(
            GETSTATIC,
            JsApiResult,
            INSTANCE,
            JsApiResult_DESC,
        )
        methodVisitor.visitVarInsn(ASTORE, 7)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 8)
        methodVisitor.visitVarInsn(ALOAD, 5)
        methodVisitor.visitVarInsn(ASTORE, 9)
        methodVisitor.visitVarInsn(ALOAD, 7)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            JsApiResult,
            "getMoshi",
            "()Lcom/squareup/moshi/Moshi;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 10)
        methodVisitor.visitLdcInsn("Ok")
        methodVisitor.visitVarInsn(ASTORE, 11)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 12)
        methodVisitor.visitTypeInsn(NEW, JsCommonResp)
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitVarInsn(ILOAD, 6)
        methodVisitor.visitVarInsn(ALOAD, 11)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 9)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            JsCommonResp,
            "<init>",
            "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 13)
        methodVisitor.visitLdcInsn(Type.getType(JsCommonResp_DESC))
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitInsn(ICONST_1)
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/reflect/Type")
        methodVisitor.visitVarInsn(ASTORE, 14)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"))
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "com/squareup/moshi/Types",
            "newParameterizedType",
            "(Ljava/lang/reflect/Type;[Ljava/lang/reflect/Type;)Ljava/lang/reflect/ParameterizedType;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 15)
        methodVisitor.visitVarInsn(ALOAD, 10)
        methodVisitor.visitVarInsn(ALOAD, 15)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/squareup/moshi/Moshi",
            "adapter",
            "(Ljava/lang/reflect/Type;)Lcom/squareup/moshi/JsonAdapter;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 14)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitVarInsn(ALOAD, 13)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/squareup/moshi/JsonAdapter",
            "toJson",
            "(Ljava/lang/Object;)Ljava/lang/String;",
            false,
        )
        methodVisitor.visitInsn(NOP)
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(6, 16)
    }

    fun twoMethod(
        clazz: String,
        superClass: String,
        methodVisitor: MethodVisitor,
        methods: List<JsMethodInfo>,
    ) {
        val jsMethod1 = methods[0]
        val jsMethod2 = methods[1]
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitVarInsn(ASTORE, 6)
        methodVisitor.visitVarInsn(ALOAD, 6)
        methodVisitor.visitLdcInsn(jsMethod1.name)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "areEqual",
            "(Ljava/lang/Object;Ljava/lang/Object;)Z",
            false,
        )
        val label0 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 3)
        if (jsMethod2.paramNum == 3) {
            methodVisitor.visitVarInsn(ALOAD, 2)
            methodVisitor.visitVarInsn(ALOAD, 4)
        }
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            clazz,
            jsMethod1.name,
            jsMethod1.descriptor,
            false,
        )
        val label1 = Label()
        methodVisitor.visitJumpInsn(GOTO, label1)
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(ALOAD, 6)
        methodVisitor.visitLdcInsn(jsMethod2.name)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "areEqual",
            "(Ljava/lang/Object;Ljava/lang/Object;)Z",
            false,
        )
        val label2 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label2)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 3)
        if (jsMethod2.paramNum == 3) {
            methodVisitor.visitVarInsn(ALOAD, 2)
            methodVisitor.visitVarInsn(ALOAD, 4)
        }
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            clazz,
            jsMethod2.name,
            jsMethod2.descriptor,
            false,
        )
        if (jsMethod2.returnVoidValue) {
            methodVisitor.visitFieldInsn(GETSTATIC, UNIT, INSTANCE, UNIT_DESC)
        }
        methodVisitor.visitJumpInsn(GOTO, label1)
        methodVisitor.visitLabel(label2)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 4)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            superClass,
            C.FUN_DISPATCHJSEVENT_NAME,
            C.FUN_DISPATCHJSEVENT_DESCRIPTOR,
            false,
        )
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitLabel(label1)
        methodVisitor.visitVarInsn(ASTORE, 5)
        methodVisitor.visitVarInsn(ALOAD, 5)
        methodVisitor.visitFieldInsn(GETSTATIC, UNIT, INSTANCE, UNIT_DESC)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "areEqual",
            "(Ljava/lang/Object;Ljava/lang/Object;)Z",
            false,
        )
        val label3 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label3)
        methodVisitor.visitInsn(ICONST_1)
        val label4 = Label()
        methodVisitor.visitJumpInsn(GOTO, label4)
        methodVisitor.visitLabel(label3)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLabel(label4)
        methodVisitor.visitVarInsn(ISTORE, 6)
        methodVisitor.visitFieldInsn(
            GETSTATIC,
            JsApiResult,
            INSTANCE,
            JsApiResult_DESC,
        )
        methodVisitor.visitVarInsn(ASTORE, 7)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 8)
        methodVisitor.visitVarInsn(ALOAD, 5)
        methodVisitor.visitVarInsn(ASTORE, 9)
        methodVisitor.visitVarInsn(ALOAD, 7)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            JsApiResult,
            "getMoshi",
            "()Lcom/squareup/moshi/Moshi;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 10)
        methodVisitor.visitLdcInsn("Ok")
        methodVisitor.visitVarInsn(ASTORE, 11)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 12)
        methodVisitor.visitTypeInsn(NEW, JsCommonResp)
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitVarInsn(ILOAD, 6)
        methodVisitor.visitVarInsn(ALOAD, 11)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 9)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            JsCommonResp,
            "<init>",
            "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 13)
        methodVisitor.visitLdcInsn(Type.getType(JsCommonResp_DESC))
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitInsn(ICONST_1)
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/reflect/Type")
        methodVisitor.visitVarInsn(ASTORE, 14)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"))
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "com/squareup/moshi/Types",
            "newParameterizedType",
            "(Ljava/lang/reflect/Type;[Ljava/lang/reflect/Type;)Ljava/lang/reflect/ParameterizedType;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 15)
        methodVisitor.visitVarInsn(ALOAD, 10)
        methodVisitor.visitVarInsn(ALOAD, 15)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/squareup/moshi/Moshi",
            "adapter",
            "(Ljava/lang/reflect/Type;)Lcom/squareup/moshi/JsonAdapter;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 14)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitVarInsn(ALOAD, 13)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/squareup/moshi/JsonAdapter",
            "toJson",
            "(Ljava/lang/Object;)Ljava/lang/String;",
            false,
        )
        methodVisitor.visitInsn(NOP)
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(6, 16)
    }

    fun threeOrMoreMethod(
        clazz: String,
        superClass: String,
        methodVisitor: MethodVisitor,
        methods: List<JsMethodInfo>,
    ) {
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitVarInsn(ASTORE, 6)
        methodVisitor.visitVarInsn(ALOAD, 6)
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false)
        val label3 = Label()
        val labelIn = mutableListOf<Label>()
        val labelOut = mutableListOf<Label>()
        val hashCodes = mutableListOf<Int>()
        methods.forEach {
            labelIn.add(Label())
            labelOut.add(Label())
            hashCodes.add(it.hashCode)
        }
        methodVisitor.visitLookupSwitchInsn(
            label3,
            hashCodes.toIntArray(),
            labelIn.toTypedArray(),
        )
        methods.forEachIndexed { index, jsMethodInfo ->
            methodVisitor.visitLabel(labelIn[index])
            methodVisitor.visitVarInsn(ALOAD, 6)
            methodVisitor.visitLdcInsn(jsMethodInfo.name)
            methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z",
                false,
            )
            methodVisitor.visitJumpInsn(IFNE, labelOut[index])
            methodVisitor.visitJumpInsn(GOTO, label3)
        }

        val label7 = Label()
        methods.forEachIndexed { index, jsMethodInfo ->
            methodVisitor.visitLabel(labelOut[index])
            methodVisitor.visitVarInsn(ALOAD, 0)
            methodVisitor.visitVarInsn(ALOAD, 3)
            if (jsMethodInfo.paramNum == 3) {
                methodVisitor.visitVarInsn(ALOAD, 2)
                methodVisitor.visitVarInsn(ALOAD, 4)
            }
            methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                clazz,
                jsMethodInfo.name,
                jsMethodInfo.descriptor,
                false,
            )
            if (jsMethodInfo.returnVoidValue) {
                methodVisitor.visitFieldInsn(GETSTATIC, UNIT, INSTANCE, UNIT_DESC)
            }
            methodVisitor.visitJumpInsn(GOTO, label7)
        }

        methodVisitor.visitLabel(label3)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 4)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            superClass,
            C.FUN_DISPATCHJSEVENT_NAME,
            C.FUN_DISPATCHJSEVENT_DESCRIPTOR,
            false,
        )
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitLabel(label7)
        methodVisitor.visitVarInsn(ASTORE, 5)
        methodVisitor.visitVarInsn(ALOAD, 5)
        methodVisitor.visitFieldInsn(GETSTATIC, UNIT, INSTANCE, UNIT_DESC)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "areEqual",
            "(Ljava/lang/Object;Ljava/lang/Object;)Z",
            false,
        )
        val label8 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label8)
        methodVisitor.visitInsn(ICONST_1)
        val label9 = Label()
        methodVisitor.visitJumpInsn(GOTO, label9)
        methodVisitor.visitLabel(label8)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLabel(label9)
        methodVisitor.visitVarInsn(ISTORE, 6)
        methodVisitor.visitFieldInsn(
            GETSTATIC,
            JsApiResult,
            INSTANCE,
            JsApiResult_DESC,
        )
        methodVisitor.visitVarInsn(ASTORE, 7)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 8)
        methodVisitor.visitVarInsn(ALOAD, 5)
        methodVisitor.visitVarInsn(ASTORE, 9)
        methodVisitor.visitVarInsn(ALOAD, 7)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            JsApiResult,
            "getMoshi",
            "()Lcom/squareup/moshi/Moshi;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 10)
        methodVisitor.visitLdcInsn("Ok")
        methodVisitor.visitVarInsn(ASTORE, 11)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 12)
        methodVisitor.visitTypeInsn(NEW, JsCommonResp)
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitVarInsn(ILOAD, 6)
        methodVisitor.visitVarInsn(ALOAD, 11)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 9)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            JsCommonResp,
            "<init>",
            "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 13)
        methodVisitor.visitLdcInsn(Type.getType(JsCommonResp_DESC))
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitInsn(ICONST_1)
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/reflect/Type")
        methodVisitor.visitVarInsn(ASTORE, 14)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/Object;"))
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "com/squareup/moshi/Types",
            "newParameterizedType",
            "(Ljava/lang/reflect/Type;[Ljava/lang/reflect/Type;)Ljava/lang/reflect/ParameterizedType;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 15)
        methodVisitor.visitVarInsn(ALOAD, 10)
        methodVisitor.visitVarInsn(ALOAD, 15)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/reflect/Type")
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/squareup/moshi/Moshi",
            "adapter",
            "(Ljava/lang/reflect/Type;)Lcom/squareup/moshi/JsonAdapter;",
            false,
        )
        methodVisitor.visitVarInsn(ASTORE, 14)
        methodVisitor.visitVarInsn(ALOAD, 14)
        methodVisitor.visitVarInsn(ALOAD, 13)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/squareup/moshi/JsonAdapter",
            "toJson",
            "(Ljava/lang/Object;)Ljava/lang/String;",
            false,
        )
        methodVisitor.visitInsn(NOP)
        methodVisitor.visitInsn(ARETURN)
        methodVisitor.visitMaxs(6, 16)
    }
}
