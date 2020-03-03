/**
 * Copyright 2020 Michel Kremer (kremi151)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lu.kremi151.chatster.plugin.retro

import lu.kremi151.chatster.api.message.Message
import lu.kremi151.chatster.api.profile.ProfileLauncher
import lu.kremi151.chatster.plugin.retro.service.RetroStateHolder
import lu.kremi151.chatster.plugin.retro.state.BuildingState
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder
import java.lang.invoke.MethodHandles
import java.nio.file.Files
import java.util.regex.Pattern
import kotlin.random.Random

object StateHandler {

    private val LOGGER: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private val random = Random(System.currentTimeMillis())
    private val client = OkHttpClient.Builder().build()

    private val REGEX_PATTERN = Pattern.compile("<img\\s+src=[\"']([^\"']+)")

    fun handleMessage(stateHolder: RetroStateHolder, message: Message, profile: ProfileLauncher, state: BuildingState?) {
        //val state = stateHolder.getState(message.senderId) ?: stateHolder.createState(message.senderId)
        if (state == null) {
            stateHolder.createState(message.sender)
            profile.sendTextMessage(message, "What should be put on the first line?")
            return
        }
        if (message.message.isNullOrBlank()) {
            profile.sendTextMessage(message, "I'm sorry, could you repeat that?\n(Type \"cancel\" to cancel)")
            return
        } else if (message.message == "cancel") {
            stateHolder.wipeState(message.sender)
            profile.sendTextMessage(message, "very canceled. much sad.")
            return
        }
        if (state.line1.isNullOrBlank()) {
            state.line1 = message.message!!.trim()
            profile.sendTextMessage(message, "What should be put on the second line?")
        } else if (state.line2.isNullOrBlank()) {
            state.line2 = message.message!!.trim()
            profile.sendTextMessage(message, "What should be put on the third line?")
        } else if (state.line3.isNullOrBlank()) {
            state.line3 = message.message!!.trim()
            val sb = StringBuilder("Which background style do you want?\nChoices:")
            for (style in BuildingState.BackgroundStyle.values()) {
                sb.append("\n* ${style.value} (${style.code})")
            }
            sb.append("\nPlease type in the numerical value of choice")
            profile.sendTextMessage(message, sb.toString())
        } else if (state.backgroundStyle == null) {
            val bgStyle = findBackgroundStyle(message.message!!)
            if (bgStyle == null) {
                profile.sendTextMessage(message, "Unknown background style: ${message.message}")
                return
            }
            state.backgroundStyle = bgStyle

            val sb = StringBuilder("Which text style do you want?\nChoices:")
            for (style in BuildingState.TextStyle.values()) {
                sb.append("\n* ${style.value} (${style.code})")
            }
            sb.append("\nPlease type in the numerical value of choice")
            profile.sendTextMessage(message, sb.toString())
        } else if (state.textStyle == null) {
            val textStyle = findTextStyle(message.message!!)
            if (textStyle == null) {
                profile.sendTextMessage(message, "Unknown text style: ${message.message}")
                return
            }
            state.textStyle = textStyle
            stateHolder.wipeState(message.sender)
            sendRequest(message, state, profile)
        }
    }

    private fun sendRequest(message: Message, state: BuildingState, profile: ProfileLauncher) {
        val requestBody = FormBody.Builder()
                .add("bcg", "${state.backgroundStyle!!.code}")
                .add("txt", "${state.textStyle!!.code}")
                .add("text1", state.line1!!)
                .add("text2", state.line2!!)
                .add("text3", state.line3!!)
                .build()
        val request = Request.Builder()
                .url("https://photofunia.com/categories/all_effects/retro-wave?server=${random.nextInt(10)}")
                .post(requestBody)
                .build()
        val html: String? = client.newCall(request).execute().use {
            val body = it.body
            body?.string()
        }

        if (html.isNullOrBlank()) {
            profile.sendTextMessage(message, "Text could not be generated")
            return
        }

        val matcher = REGEX_PATTERN.matcher(html)
        val url = if (matcher.find()) {
            matcher.group(1).trim { it <= ' ' }
        } else {
            null
        }

        if (url.isNullOrBlank()) {
            profile.sendTextMessage(message, "Text could not be generated")
            return
        }

        val tempDir = Files.createTempDirectory("retro").toFile()
        if (!tempDir.mkdirs() && !tempDir.exists()) {
            LOGGER.warn("Could not create temporary dir")
        }
        val tempFile = File(tempDir, "retro-${System.currentTimeMillis()}")

        val dlRequest = Request.Builder().url(url).build()
        client.newCall(dlRequest).execute().use { response ->
            FileOutputStream(tempFile).use { out ->
                val body = response.body ?: throw IOException("Could not read response")
                body.byteStream().use { bodyStream ->
                    val buffer = ByteArray(512)
                    var length: Int
                    while(true) {
                        length = bodyStream.read(buffer)
                        if (length == -1) {
                            break
                        }
                        out.write(buffer, 0, length)
                        out.flush()
                    }
                }
            }
        }

        try {
            profile.sendTextMessage(message, tempFile)
        } finally {
            if (!deleteFolder(tempDir)) {
                LOGGER.warn("Could not delete temporary folder at {}", tempDir.absolutePath)
            }
        }
    }

    private fun findBackgroundStyle(input: String): BuildingState.BackgroundStyle? {
        for (style in BuildingState.BackgroundStyle.values()) {
            if ("${style.code}" == input || style.value == input) {
                return style
            }
        }
        return null
    }

    private fun findTextStyle(input: String): BuildingState.TextStyle? {
        for (style in BuildingState.TextStyle.values()) {
            if ("${style.code}" == input || style.value == input) {
                return style
            }
        }
        return null
    }

    private fun deleteFolder(file: File): Boolean {
        var result = true
        if (file.isDirectory) {
            for (child in file.listFiles()) {
                result = result and deleteFolder(child)
            }
        }
        return file.delete() && result
    }

}