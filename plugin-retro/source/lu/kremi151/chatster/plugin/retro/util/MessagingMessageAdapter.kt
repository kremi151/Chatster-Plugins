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

package lu.kremi151.chatster.plugin.retro.util

import lu.kremi151.chatster.api.message.Message
import lu.kremi151.chatster.api.message.SenderReference
import lu.kremi151.chatster.api.profile.ProfileLauncher
import java.io.File

class MessagingMessageAdapter(
        private val msg: Message,
        private val profile: ProfileLauncher
): MessagingAdapter {

    override val sender: SenderReference get() = msg.sender

    override val message: String? get() = msg.message

    override fun sendTextMessage(message: String) {
        profile.sendTextMessage(msg, message)
    }

    override fun sendFile(file: File) {
        profile.sendTextMessage(msg, file)
    }

}
