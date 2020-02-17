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

import lu.kremi151.chatster.api.service.CredentialStore

class VaultfileCredentialStore(
        private val plugin: VaultfilePlugin,
        private val config: VaultfileConfig
): CredentialStore {

    companion object {
        private const val DEFAULT_VAULTFILE_NAME = "credentials.vault"
    }

    override fun readCredential(name: String): String? {
        var command = "read-secret --file ${config.vaultfileName ?: DEFAULT_VAULTFILE_NAME}"
        val customKeyPath = config.keyFile?.path
        if (customKeyPath != null) {
            command = "$command --key-file \"$customKeyPath\""
        }
        val customKeyName = config.keyFile?.keyName
        if (customKeyName != null) {
            command = "$command --key-name $customKeyName"
        }
        command = "$command --name $name"
        return plugin.executeVaultfileCommand(command)
    }

    override fun storeCredential(name: String, value: String) {
        var command = "add-secret --file ${config.vaultfileName ?: DEFAULT_VAULTFILE_NAME}"
        val customKeyPath = config.keyFile?.path
        if (customKeyPath != null) {
            command = "$command --key-file \"$customKeyPath\""
        }
        val customKeyName = config.keyFile?.keyName
        if (customKeyName != null) {
            command = "$command --key-name $customKeyName"
        }
        command = "$command --name $name --value $value"
        plugin.executeVaultfileCommand(command)
    }

}