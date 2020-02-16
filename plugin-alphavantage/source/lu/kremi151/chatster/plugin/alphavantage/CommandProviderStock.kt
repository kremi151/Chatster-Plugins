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

package lu.kremi151.chatster.plugin.alphavantage

import com.fasterxml.jackson.databind.ObjectMapper
import lu.kremi151.chatster.api.command.*
import lu.kremi151.jector.annotations.Inject
import okhttp3.OkHttpClient
import java.lang.invoke.MethodHandles
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.io.IOException
import lu.kremi151.chatster.api.context.CommandContext
import lu.kremi151.chatster.plugin.alphavantage.json.AlphaVantageConfig
import lu.kremi151.chatster.plugin.alphavantage.json.GlobalQuote
import lu.kremi151.chatster.plugin.alphavantage.json.GlobalQuoteResponse
import lu.kremi151.chatster.plugin.alphavantage.json.MatchesResult
import okhttp3.Request
import org.slf4j.LoggerFactory

class CommandProviderStock(
        private val config: AlphaVantageConfig
): CommandProvider {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
        private const val ARG_SYMBOL = "symbol"
    }

    private val client: OkHttpClient = OkHttpClient.Builder().build()

    @Inject
    private lateinit var objectMapper: ObjectMapper

    override fun registerCommands(builder: RootCommandBuilder, registry: CommandRegistry) {
        val alphaVantageApiKey = config.alphaVantageApiKey
        if (alphaVantageApiKey == null || alphaVantageApiKey.isBlank()) {
            LOGGER.warn("No API key configured for the AlphaVantage plugin. The stock command will not be registered.")
            return
        }
        registry.registerCommand(builder.literal("stock")
                .argGreedyString(ARG_SYMBOL)
                .executes(object : CommandExecutor{
                    override fun execute(command: ExecutedCommand): Boolean {
                        val symbol = command.getStringArgument(ARG_SYMBOL)
                        if (!queryStock(symbol, command.context, alphaVantageApiKey)) {
                            command.context.sendTextMessage("No data could be loaded for $symbol")
                        }
                        return true
                    }
                })
                .top())
    }

    private fun queryStock(symbol: String, context: CommandContext, alphaVantageApiKey: String): Boolean {
        context.sendWriting(true)

        val request: Request
        try {
            request = Request.Builder()
                    .url("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + URLEncoder.encode(symbol, "UTF-8") + "&apikey=" + alphaVantageApiKey)
                    .build()
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body ?: return false
                val globalQuoteResponse = objectMapper.readValue(responseBody.string(), GlobalQuoteResponse::class.java)
                        ?: return false
                val globalQuote = globalQuoteResponse.globalQuote
                if (globalQuote != null && symbol == globalQuote.symbol) {
                    respondStock(globalQuote, context)
                    return true
                }
                lookupSymbol(symbol, context, alphaVantageApiKey)
                return true
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun respondStock(quote: GlobalQuote, context: CommandContext) {
        val sb = StringBuilder()
        sb.append("Stock data for ").append(quote.symbol).append("\n\n")
        sb.append("Open: ").append(quote.open).append(" USD\n")
        sb.append("High: ").append(quote.high).append(" USD\n")
        sb.append("Low: ").append(quote.low).append(" USD\n")
        sb.append("Price: ").append(quote.price).append(" USD\n")
        sb.append("Volume: ").append(quote.volume).append('\n')
        sb.append("Latest trading day: ").append(quote.latestTradingDay).append('\n')
        sb.append("Previous close: ").append(quote.previousClose).append(" USD\n")
        sb.append("Change: ")
        val change = quote.change
        when {
            change == null -> sb.append("?")
            change.startsWith("-") -> sb.append("\uD83D\uDD3D")
            else -> sb.append("\uD83D\uDD3C")
        }
        sb.append(" ").append(quote.change).append(" (").append(quote.changePercent).append(")")
        context.sendTextMessage(sb.toString())
    }

    @Throws(IOException::class)
    private fun lookupSymbol(input: String, context: CommandContext, alphaVantageApiKey: String): Boolean {
        val request: Request
        try {
            request = Request.Builder()
                    .url("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + URLEncoder.encode(input, "UTF-8") + "&apikey=" + alphaVantageApiKey)
                    .build()
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

        client.newCall(request).execute().use { response ->
            val responseBody = response.body ?: return false
            val matchesResult = objectMapper.readValue(responseBody.string(), MatchesResult::class.java) ?: return false
            val bestMatches = matchesResult.bestMatches
            if (bestMatches == null || bestMatches.isEmpty()) {
                context.sendTextMessage("Sorry, no stock data was found for $input")
                return false
            }

            val sb = StringBuilder("\u274C No stock data was found for ")
            sb.append(input).append("\n\nDid you mean:\n")
            for (match in bestMatches) {
                sb.append("\u25FE ").append(match.name).append(" (").append(match.symbol)
                        .append(") [").append(match.currency).append("]\n")
            }

            context.sendTextMessage(sb.toString())
            return true
        }
    }

}