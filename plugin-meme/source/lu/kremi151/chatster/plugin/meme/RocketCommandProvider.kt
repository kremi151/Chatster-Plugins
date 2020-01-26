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

class RocketCommandProvider: CommandProvider {

    companion object {
        private const val ARG_AMOUNT = "amount"
    }

    override fun registerCommands(builder: RootCommandBuilder, registry: CommandRegistry) {
        registry.registerCommand(builder.literal("rocket")
                .argInteger(ARG_AMOUNT)
                .executes(object : CommandExecutor {
                    override fun execute(command: ExecutedCommand): Boolean {
                        val amount = command.getIntArgument(ARG_AMOUNT)
                        if (amount <= 0) {
                            return false
                        }
                        val sb = StringBuilder()
                        for (i in 0 until amount) {
                            sb.append("\uD83D\uDE80")
                        }
                        command.context.sendTextMessage(sb.toString())
                        return true
                    }
                })
                .top())
    }

}