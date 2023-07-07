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

package jm.droid.lib.bridge.core

import android.graphics.Bitmap
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebView
import com.just.agentweb.WebChromeClient

class DefaultWebChromeClient(
    private val webAbilitiesClient: WebAbilitiesClient?,
) : WebChromeClient() {
    override fun onReceivedTitle(view: WebView, title: String) {
        super.onReceivedTitle(view, title)
        if (title.isEmpty() || view.url?.startsWith("data:") == true) {
            webAbilitiesClient?.onWebTitle("")
        } else {
            webAbilitiesClient?.onWebTitle(title)
        }
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        consoleMessage?.let {
            Log.v("js-log", "${it.message()} - (${it.sourceId()}:${it.lineNumber()})")
        }
        return super.onConsoleMessage(consoleMessage)
    }
}
