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

class VaultfileCredentialStore(private val plugin: VaultfilePlugin): CredentialStore {

    override fun readCredential(name: String): String? {
        return plugin.executeVaultfileCommand("read-secret -f ${plugin.vaultfileName} -n $name", true)
    }

    override fun storeCredential(name: String, value: String) {
        plugin.executeVaultfileCommand("add-secret --file ${plugin.vaultfileName} --name $name --value $value", true)
    }

}