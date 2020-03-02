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

package lu.kremi151.chatster.plugin.retro.service.impl

import lu.kremi151.chatster.api.message.Message
import lu.kremi151.chatster.api.profile.ProfileLauncher
import lu.kremi151.chatster.api.service.MessageHandler
import lu.kremi151.chatster.plugin.retro.StateHandler
import lu.kremi151.chatster.plugin.retro.service.RetroStateHolder
import lu.kremi151.jector.annotations.Inject

class RetroMessageHandler: MessageHandler {

    @Inject private lateinit var stateHolder: RetroStateHolder

    override fun onInboundMessage(message: Message, profile: ProfileLauncher): Message? {
        val state = stateHolder.getState(message.senderId)
        return if (state == null) {
            message
        } else {
            StateHandler.handleMessage(stateHolder, message, profile, state)
            null
        }
    }

}