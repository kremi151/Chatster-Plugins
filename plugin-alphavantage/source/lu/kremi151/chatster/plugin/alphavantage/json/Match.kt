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

package lu.kremi151.chatster.plugin.alphavantage.json

import com.fasterxml.jackson.annotation.JsonProperty

class Match {

    @JsonProperty("1. symbol")
    var symbol: String? = null

    @JsonProperty("2. name")
    var name: String? = null

    @JsonProperty("3. type")
    var type: String? = null

    @JsonProperty("4. region")
    var region: String? = null

    @JsonProperty("5. marketOpen")
    var marketOpen: String? = null

    @JsonProperty("6. marketClose")
    var marketClose: String? = null

    @JsonProperty("7. timezone")
    var timezone: String? = null

    @JsonProperty("8. currency")
    var currency: String? = null

    @JsonProperty("9. matchScore")
    var matchScore: String? = null

}