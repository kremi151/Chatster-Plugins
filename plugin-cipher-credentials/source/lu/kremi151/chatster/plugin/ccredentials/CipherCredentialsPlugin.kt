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

package lu.kremi151.chatster.plugin.ccredentials

import lu.kremi151.chatster.api.annotations.Plugin
import lu.kremi151.chatster.api.annotations.Provider
import lu.kremi151.chatster.api.plugin.ChatsterPlugin

@Plugin(id = "cipher-credentials", name = "Cipher credential storage plugin")
class CipherCredentialsPlugin: ChatsterPlugin() {

    companion object {
        private const val ENV_VAR = "CHATSTER_CREDENTIAL_STORE_KEY"
    }

    @Provider
    fun createCipherCredentialStore(): CipherCredentialStore {
        val key = System.getenv(ENV_VAR)
        if (key == null || key.isBlank()) {
            throw IllegalStateException("Could not read credential store key from environment variable $ENV_VAR")
        }
        return CipherCredentialStore(key)
    }
}