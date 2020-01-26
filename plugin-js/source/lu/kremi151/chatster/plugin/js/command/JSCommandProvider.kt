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

import com.fasterxml.jackson.databind.ObjectMapper
import lu.kremi151.chatster.api.annotations.CommandArgParam
import lu.kremi151.chatster.api.annotations.Inject
import lu.kremi151.chatster.api.command.*
import lu.kremi151.chatster.plugin.js.command.json.JSCommandArgumentDefinition
import lu.kremi151.chatster.plugin.js.command.json.JSCommandDefinition
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.*
import javax.script.ScriptException

class JSCommandProvider(
        private val configFile: File
) : CommandProvider {

    @Inject
    private lateinit var objectMapper: ObjectMapper

    private fun readDefinition(): JSCommandDefinition {
        FileReader(configFile).use { reader -> return objectMapper.readValue(reader, JSCommandDefinition::class.java) }
    }

    override fun registerCommands(builder: RootCommandBuilder, registry: CommandRegistry) {
        val definition = readDefinition()

        val scriptFile = File(configFile.parentFile, definition.script)
        if (!scriptFile.exists() || !scriptFile.isFile || !scriptFile.canRead()) {
            throw IOException("Cannot open script file at $scriptFile")
        }

        val builder1 = builder
                .literal(Objects.requireNonNull(definition.name, "Command name must not be null")!!)
        val builder2 = setupArguments(builder1, definition) as ExecutableCommandBuilder
        try {
            registry.registerCommand(builder2.executes(JSCommandExecutor(scriptFile)).top())
        } catch (e: ScriptException) {
            throw IOException(e)
        }

    }

    companion object {

        private val LOGGER: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        private fun tryApplyArgumentDefinition(builder: ArgumentableCommandBuilder, definition: JSCommandArgumentDefinition, paramSize: Int, method: Method): ArgumentableCommandBuilder? {
            val args = arrayOfNulls<Any>(paramSize)
            args[0] = definition.name

            val additionalParams = definition.getRest()

            val parameterAnnotations = method.parameterAnnotations
            for (i in 1 until parameterAnnotations.size) {
                val annotations = parameterAnnotations[i]
                for (annotation in annotations) {
                    if (annotation.annotationClass.java != CommandArgParam::class.java) {
                        continue
                    }
                    val commandArgParam = annotation as CommandArgParam
                    val additionalParam = additionalParams[commandArgParam.value] ?: return null
                    args[i] = additionalParam
                    break
                }
            }

            return try {
                method.invoke(builder, *args) as ArgumentableCommandBuilder
            } catch (e: ReflectiveOperationException) {
                LOGGER.warn("Reflection error while invoking argument builder", e)
                null
            }
        }

        private fun applyArgumentDefinition(builder: ArgumentableCommandBuilder, definition: JSCommandArgumentDefinition, paramSize: Int, methods: Array<Method>): ArgumentableCommandBuilder {
            for (candidate in methods) {
                val result = tryApplyArgumentDefinition(builder, definition, paramSize, candidate)
                if (result != null) {
                    return result
                }
            }
            throw IllegalArgumentException("No applicable argument builder found for $definition")
        }

        private fun findAndApplyCandidateArgumentBuilder(builder: ArgumentableCommandBuilder, definition: JSCommandArgumentDefinition): ArgumentableCommandBuilder {
            val type = definition.type ?: "string"
            val parameterCount = 1 + if (definition.getRest().isEmpty()) 0 else definition.getRest().size
            val methodName = "arg" + if (type.length <= 1)
                type.substring(0, 1).toUpperCase()
            else
                type.substring(0, 1).toUpperCase() + type.substring(1)
            val candidates = Arrays.stream<Method>(ArgumentableCommandBuilder::class.java.declaredMethods)
                    .filter { method -> method.name == methodName }
                    .filter { method -> method.parameterCount == parameterCount }
                    .toArray<Method> { size -> arrayOfNulls(size) }
            if (candidates.isEmpty()) {
                throw IllegalArgumentException("No valid argument builder found for $definition")
            }
            return applyArgumentDefinition(builder, definition, parameterCount, candidates)
        }

        private fun setupArguments(inBuilder: ArgumentableCommandBuilder, definition: JSCommandDefinition): ArgumentableCommandBuilder {
            var outBuilder = inBuilder
            val defArgs = definition.args
            if (defArgs == null || defArgs.isEmpty()) {
                return outBuilder
            }
            for (argDef in defArgs) {
                outBuilder = findAndApplyCandidateArgumentBuilder(outBuilder, argDef)
            }
            return outBuilder
        }
    }
}