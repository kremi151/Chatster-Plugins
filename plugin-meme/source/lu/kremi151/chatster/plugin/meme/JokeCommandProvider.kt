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
import java.io.IOException
import java.util.regex.Pattern

class JokeCommandProvider: CommandProvider {

    companion object {
        private val ONE_LINER_PATTERN = Pattern.compile("<P>\\s*([^<]+)\\s*")
    }

    private val client = OkHttpClient.Builder().build()

    @Throws(IOException::class)
    private fun handleJoke(context: CommandContext) {
        context.sendWriting(true)
        for (i in 0..2) {
            val request = Request.Builder()
                    .url("http://www.randomjoke.com/topic/oneliners.php")
                    .build()
            client.newCall(request).execute().use { response ->
                val html = response.body!!.string()
                val matcher = ONE_LINER_PATTERN.matcher(html)
                if (matcher.find()) {
                    val theJoke = matcher.group(1).trim()
                    context.sendTextMessage(theJoke)
                    return
                }
            }
        }
        context.sendTextMessage("Sorry, no joke for you today :(")
    }

    override fun registerCommands(builder: RootCommandBuilder, registry: CommandRegistry) {
        registry.registerCommand(builder.literal("joke")
                .executes(object : CommandExecutor {
                    override fun execute(command: ExecutedCommand): Boolean {
                        handleJoke(command.context)
                        return true
                    }
                })
                .top())
    }

}