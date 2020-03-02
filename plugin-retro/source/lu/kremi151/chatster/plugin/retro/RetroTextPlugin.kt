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

import lu.kremi151.chatster.api.annotations.Plugin
import lu.kremi151.chatster.api.plugin.ChatsterPlugin
import lu.kremi151.chatster.plugin.retro.service.RetroStateHolder
import lu.kremi151.chatster.plugin.retro.service.impl.RetroMessageHandler
import lu.kremi151.chatster.plugin.retro.service.impl.RetroStateHolderImpl
import lu.kremi151.jector.annotations.Provider
import lu.kremi151.jector.enums.Priority

@Plugin(id = "retro", name = "Retro text generator for Chatster")
class RetroTextPlugin: ChatsterPlugin() {

    @Provider(priority = Priority.HIGHEST)
    fun createRetroMessageHandler(): RetroMessageHandler {
        return RetroMessageHandler()
    }

    @Provider
    fun createStateHolder(): RetroStateHolder {
        return RetroStateHolderImpl()
    }

}