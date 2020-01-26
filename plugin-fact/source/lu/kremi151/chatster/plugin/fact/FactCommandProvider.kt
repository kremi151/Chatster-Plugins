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

package lu.kremi151.chatster.plugin.fact

import lu.kremi151.chatster.api.command.*
import lu.kremi151.chatster.api.context.CommandContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class FactCommandProvider: CommandProvider {

    companion object {
        private val FACT_PATTERN = Pattern.compile("<span class=\"td-sml-description\"><p>([^<]+)")
    }

    private val random = Random()
    private val client: OkHttpClient = OkHttpClient.Builder().build()

    private fun tryHandleFact(context: CommandContext): Boolean {
        val request = Request.Builder()
                .url("https://factrepublic.com/random-facts-generator/")
                .build()
        client.newCall(request).execute().use { response ->
            val html = response.body!!.string()
            val matcher = FACT_PATTERN.matcher(html)
            val facts = ArrayList<String>()
            while (matcher.find()) {
                val fact = matcher.group(1).trim()
                facts.add(fact)
            }
            if (facts.isEmpty()) {
                return false
            }
            val theFact: String
            synchronized(random) {
                theFact = facts[random.nextInt(facts.size)]
            }
            context.sendTextMessage(theFact)
            return true
        }
    }

    @Throws(IOException::class)
    private fun handleFact(context: CommandContext) {
        context.sendWriting(true)
        for (i in 0..2) {
            if (tryHandleFact(context)) {
                return
            }
        }
        context.sendTextMessage("Sorry, no fact for you today :(")
    }

    override fun registerCommands(builder: RootCommandBuilder, registry: CommandRegistry) {
        registry.registerCommand(builder.literal("fact")
                .executes(object : CommandExecutor {
                    override fun execute(command: ExecutedCommand): Boolean {
                        handleFact(command.context)
                        return true
                    }
                })
                .top())
    }

}