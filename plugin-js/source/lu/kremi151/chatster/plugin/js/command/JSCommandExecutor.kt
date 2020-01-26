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

package lu.kremi151.chatster.plugin.js.command

import lu.kremi151.chatster.api.command.CommandExecutor
import lu.kremi151.chatster.api.command.ExecutedCommand
import java.io.File
import java.io.FileReader
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngineManager

class JSCommandExecutor(script: File): CommandExecutor {

    private val engine = ScriptEngineManager().getEngineByName("nashorn")

    private val compiledScript: CompiledScript

    init {
        val compilable = engine as Compilable
        compiledScript = FileReader(script).use { reader -> compilable.compile(reader) }
    }

    override fun execute(command: ExecutedCommand): Boolean {
        val bindings = engine.createBindings()
        bindings["command"] = command
        compiledScript.eval(bindings)
        return true
    }

}