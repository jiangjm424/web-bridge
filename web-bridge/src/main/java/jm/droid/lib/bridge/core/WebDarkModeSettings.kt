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

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.webkit.WebSettings
import androidx.annotation.IntDef
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

object WebDarkModeSettings {

    const val DARK_OFF = 0
    const val DARK_AUTO = 1
    const val DARK_ON = 2

    @IntDef(value = [DARK_OFF, DARK_AUTO, DARK_ON])
    @Retention(AnnotationRetention.SOURCE)
    annotation class DarkMode

    private val darkSetting: IDarkSetting by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            DarkSettingDefault()
        } else {
            DarkSettingImplApi33()
        }
    }

    fun configDarkSetting(webSettings: WebSettings, @DarkMode mode: Int, context: Context?) {
        darkSetting.config(webSettings, mode, isDarkMode(context))
    }

    private fun isDarkMode(context: Context?): Boolean {
        return context?.let {
            val uiMode = it.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            uiMode == Configuration.UI_MODE_NIGHT_YES
        } ?: false
    }

    private interface IDarkSetting {
        fun config(webSettings: WebSettings, @DarkMode mode: Int, appDark: Boolean)
    }

    private class DarkSettingDefault : IDarkSetting {
        override fun config(webSettings: WebSettings, mode: Int, appDark: Boolean) {
            if (!WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) return
            when (mode) {
                DARK_ON -> {
                    WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                }

                DARK_OFF -> {
                    WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_OFF)
                }

                DARK_AUTO -> {
                    val dark = if (appDark) {
                        WebSettingsCompat.FORCE_DARK_ON
                    } else {
                        WebSettingsCompat.FORCE_DARK_OFF
                    }
                    WebSettingsCompat.setForceDark(webSettings, dark)
                }
            }
        }
    }

    private class DarkSettingImplApi33 : IDarkSetting {
        override fun config(webSettings: WebSettings, mode: Int, appDark: Boolean) {
            if (!WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) return
            when (mode) {
                DARK_OFF -> {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(webSettings, false)
                }

                DARK_ON, DARK_AUTO -> {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(webSettings, true)
                }
            }
        }
    }
}
