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

package lu.kremi151.chatster.plugin.vaultfile

import lu.kremi151.chatster.api.annotations.Plugin
import lu.kremi151.chatster.api.plugin.ChatsterPlugin
import lu.kremi151.chatster.api.plugin.InitPluginContext
import lu.kremi151.chatster.api.plugin.PreInitPluginContext
import lu.kremi151.chatster.api.service.CredentialStore
import lu.kremi151.jector.annotations.Provider
import org.slf4j.LoggerFactory
import java.io.*
import java.lang.StringBuilder
import java.lang.invoke.MethodHandles
import java.util.concurrent.TimeUnit

@Plugin(id = "vaultfile", name = "Vaultfile integration plugin")
class VaultfilePlugin: ChatsterPlugin() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }

    private lateinit var config: VaultfileConfig

    override fun onPreInitialize(context: PreInitPluginContext) {
        // We have to load the config in onPreInitialize as onLoad is called after bean initialization
        config = context.loadConfig(VaultfileConfig::class.java) ?: VaultfileConfig()
    }

    override fun onLoad(context: InitPluginContext) {
        LOGGER.info("Check if vaultfile executable is available...")
        executeVaultfileCommand("--version")
        LOGGER.info("Found vaultfile executable")
    }

    private fun readStreamIntoString(stream: InputStream): String {
        return BufferedReader(InputStreamReader(stream)).use {
            var line: String?
            val sb = StringBuilder()
            do {
                line = it.readLine()
                if (line == null) {
                    break
                }
                sb.append(line)
            } while (line != null)
            sb.toString()
        }
    }

    fun executeVaultfileCommand(command: String): String {
        val vaultfileBaseCmd = config.executablePath ?: "vaultfile"
        val process = Runtime.getRuntime().exec("$vaultfileBaseCmd $command")
        process.waitFor(1L, TimeUnit.SECONDS)

        val exitCode = process.exitValue()
        if (exitCode != 0) {
            LOGGER.warn("Vaultfile command failed with exit code {} and error {}", exitCode, readStreamIntoString(process.errorStream))
            throw IOException()
        }

        return readStreamIntoString(process.inputStream)
    }

    @Provider
    fun createVaultfileCredentialStore(): CredentialStore {
        return VaultfileCredentialStore(this, config)
    }

}