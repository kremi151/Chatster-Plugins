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

package lu.kremi151.chatster.plugin.meme

import lu.kremi151.chatster.api.command.*
import lu.kremi151.chatster.api.context.CommandContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.invoke.MethodHandles
import java.nio.file.Files
import java.util.*
import java.util.regex.Pattern

class MemeCommandProvider: CommandProvider {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        private val MEME_PATTERN = Pattern.compile("<img\\s+id=[\"']im[\"'].+src=[\"']([^\"']+)")
        private val TITLE_PATTERN = Pattern.compile("<h1 id=[\"']img-title[\"']>([^<]+)")
        private val EXTENSION_PATTERN = Pattern.compile("\\.([a-zA-Z0-9]+)$")

        private fun sanitizeFilename(inputName: String): String {
            return inputName.replace("[^a-zA-Z0-9-_.]".toRegex(), "_")
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

    private val client = OkHttpClient.Builder().build()

    @Throws(IOException::class)
    private fun handleMeme(context: CommandContext) {
        context.sendWriting(true)
        var request = Request.Builder()
                .url("https://imgflip.com/i/")
                .build()
        var memeUrl: String? = null
        var memeTitle: String? = null
        for (i in 0..2) {
            memeUrl = client.newCall(request).execute().use { response ->
                val html = response.body!!.string()
                var matcher = TITLE_PATTERN.matcher(html)
                if (matcher.find()) {
                    memeTitle = matcher.group(1).trim { it <= ' ' }
                }
                matcher = MEME_PATTERN.matcher(html)
                if (matcher.find()) {
                    return@use matcher.group(1).trim { it <= ' ' }
                } else {
                    return@use null
                }
            }
            if (memeUrl != null) {
                break
            }
        }
        if (memeUrl == null) {
            context.sendTextMessage("Sorry, no meme for you today :(")
            return
        }
        if (memeTitle == null) {
            memeTitle = "meme_" + UUID.randomUUID()
        }

        var fileName = sanitizeFilename(memeTitle!!)
        if (fileName.length > 16) {
            fileName = fileName.substring(0, 16)
        }

        val matcher = EXTENSION_PATTERN.matcher(memeUrl)
        if (matcher.find()) {
            fileName += "." + matcher.group(1)
        }

        val tempDir = Files.createTempDirectory("meme").toFile()
        if (!tempDir.mkdirs() && !tempDir.exists()) {
            LOGGER.warn("Could not create temporary dir")
        }
        val tempFile = File(tempDir, fileName)

        if (memeUrl.startsWith("//")) {
            memeUrl = "https:$memeUrl"
        }

        request = Request.Builder().url(memeUrl).build()
        client.newCall(request).execute().use { response ->
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
            context.sendTextMessage(tempFile)
        } finally {
            if (!deleteFolder(tempDir)) {
                LOGGER.warn("Could not delete temporary folder at {}", tempDir.absolutePath)
            }
        }
    }

    override fun registerCommands(builder: RootCommandBuilder, registry: CommandRegistry) {
        registry.registerCommand(builder.literal("meme")
                .executes(object : CommandExecutor {
                    override fun execute(command: ExecutedCommand): Boolean {
                        handleMeme(command.context)
                        return true
                    }
                })
                .top())
    }

}