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

package lu.kremi151.chatster.plugin.js

import lu.kremi151.chatster.api.annotations.Plugin
import lu.kremi151.chatster.api.command.CommandProvider
import lu.kremi151.chatster.api.plugin.ChatsterPlugin
import lu.kremi151.chatster.api.util.Handler
import lu.kremi151.chatster.plugin.js.command.JSCommandProvider
import java.io.File

@Plugin(id = "javascript", name = "JavaScript integration for Chatster")
class JavaScriptPlugin: ChatsterPlugin() {

    private var commandProviders: List<JSCommandProvider> = emptyList()

    override fun onLoad() {
        val scriptsFolder = this.scriptsFolder
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs()
        }
        val providers = ArrayList<JSCommandProvider>()
        val jsonFiles = scriptsFolder.listFiles { file -> file.name.toLowerCase().endsWith(".json")}
        if (jsonFiles != null) {
            for (file in jsonFiles) {
                if (!file.isFile || !file.canRead()) {
                    continue
                }
                providers.add(JSCommandProvider(file))
            }
        }
        commandProviders = providers
    }

    override fun onRegisterCommands(register: Handler<CommandProvider>) {
        for (provider in commandProviders) {
            register(provider)
        }
    }

    private val scriptsFolder = File("js-commands")

}