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
import lu.kremi151.chatster.api.annotations.Provider
import lu.kremi151.chatster.api.plugin.ChatsterPlugin
import lu.kremi151.chatster.api.service.CredentialStore
import org.slf4j.LoggerFactory
import java.io.*
import java.lang.StringBuilder
import java.lang.invoke.MethodHandles
import java.util.*
import java.util.concurrent.TimeUnit


@Plugin(id = "vaultfile", name = "Vaultfile integration plugin")
class VaultfilePlugin: ChatsterPlugin() {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }

    private val properties = Properties()

    override fun onPreInitialize() {
        val propertiesFile = File("vaultfile.properties")
        if (propertiesFile.exists()) {
            FileInputStream(propertiesFile).use { properties.load(it) }
        }
    }

    override fun onLoad() {
        LOGGER.info("Check if vaultfile executable is available...")
        executeVaultfileCommand("--version")
        LOGGER.info("Found vaultfile executable")
    }

    val customVaultfilePath: String? get() = properties.getProperty("vaultfile.executable.path", null)
    val customKeyPath: String? get() = properties.getProperty("vaultfile.keyfile.path", null)
    val customKeyName: String? get() = properties.getProperty("vaultfile.keyfile.keyname", null)
    val vaultfileName: String get() = properties.getProperty("vaultfile.name", "credentials.vault")

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
        val customVaultfilePath = this.customVaultfilePath
        val vaultfileBaseCmd = if (customVaultfilePath != null) {
            "$customVaultfilePath"
        } else {
            "vaultfile"
        }

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
        return VaultfileCredentialStore(this)
    }

}