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

class GlobalQuote {

    @JsonProperty("01. symbol")
    var symbol: String? = null

    @JsonProperty("02. open")
    var open: String? = null

    @JsonProperty("03. high")
    var high: String? = null

    @JsonProperty("04. low")
    var low: String? = null

    @JsonProperty("05. price")
    var price: String? = null

    @JsonProperty("06. volume")
    var volume: String? = null

    @JsonProperty("07. latest trading day")
    var latestTradingDay: String? = null

    @JsonProperty("08. previous close")
    var previousClose: String? = null

    @JsonProperty("09. change")
    var change: String? = null

    @JsonProperty("10. change percent")
    var changePercent: String? = null

}
