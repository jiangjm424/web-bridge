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

package jm.droid.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class WebBridgePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.getByType(AndroidComponentsExtension::class.java).apply {
                onVariants {
                    println("enable web bridge plugin")
                    it.instrumentation.transformClassesWith(WebBridgeTransform::class.java, InstrumentationScope.PROJECT) {}
                    it.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
                }
            }
//            pluginManager.withPlugin("com.android.application") {
//                println("in app")
//            }
//            pluginManager.withPlugin("com.android.library") {
//                println("in lib")
//                val bb = extensions.getByType(AndroidComponentsExtension::class.java)
//                bb.onVariants { variant ->
//                    println("variant name: ${variant.name}")
//                    variant.instrumentation.transformClassesWith(
//                        WebTransform::class.java,
//                        InstrumentationScope.PROJECT
//                    ) {}
//                    variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
//                }
//            }
        }
    }
}
