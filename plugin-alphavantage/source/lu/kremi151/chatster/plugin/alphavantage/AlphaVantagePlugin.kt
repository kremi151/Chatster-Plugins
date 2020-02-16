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

package lu.kremi151.chatster.plugin.alphavantage

import lu.kremi151.chatster.api.annotations.Plugin
import lu.kremi151.chatster.api.command.CommandProvider
import lu.kremi151.chatster.api.plugin.ChatsterPlugin
import lu.kremi151.chatster.api.plugin.PluginContext
import lu.kremi151.chatster.api.util.Handler
import lu.kremi151.chatster.plugin.alphavantage.json.AlphaVantageConfig

@Plugin(id = "alphavantage", name = "AlphaVantage stock command plugin")
class AlphaVantagePlugin: ChatsterPlugin() {

    private lateinit var config: AlphaVantageConfig

    override fun onLoad(event: PluginContext) {
        var config = event.loadConfig(AlphaVantageConfig::class.java)
        if (config == null) {
            config = AlphaVantageConfig()
            event.saveConfig(config)
        }
        this.config = config
    }

    override fun onRegisterCommands(register: Handler<CommandProvider>) {
        register(CommandProviderStock(config))
    }

}