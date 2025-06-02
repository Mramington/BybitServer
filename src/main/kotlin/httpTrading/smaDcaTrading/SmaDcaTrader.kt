package com.example.httpTrading.smaDcaTrading

import com.example.SuperInfo
import com.example.httpTrading.encryption.hmacSha256Hex
import com.example.httpTrading.server.BybitServer
import com.example.httpTrading.smaDcaTrading.serialization.Category
import com.example.httpTrading.smaDcaTrading.serialization.Kline
import com.example.httpTrading.smaDcaTrading.serialization.KlineRes
import com.example.httpTrading.smaDcaTrading.serialization.LastPrice
import com.example.httpTrading.smaDcaTrading.serialization.Order
import com.example.httpTrading.smaDcaTrading.serialization.OrderType
import com.example.httpTrading.smaDcaTrading.serialization.PriceResponse
import com.example.httpTrading.smaDcaTrading.serialization.RetMsg
import com.example.httpTrading.smaDcaTrading.serialization.Side
import com.example.model.SmaDcaStrategy
import com.example.model.SmaDcaStrategyRepository
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SmaDcaTrader(
    private val smaDcaStrategyRepository: SmaDcaStrategyRepository,
) {
    private val json =
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    init {
        println("init SmaDcaTrader")
        CoroutineScope(Dispatchers.Default).launch {
            smaDcaStrategyRepository.allSmaDcaStrategies().forEach {
                launch {
                    var fl = processSmaDcaStrategy(it)
                    while (fl)
                        fl = processSmaDcaStrategy(it)
                }
            }
        }
    }

    suspend fun processSmaDcaStrategy(smaDcaStrategy: SmaDcaStrategy): Boolean {
        val dcaInterval = smaDcaStrategy.dcaInterval.toLong()
        var lastOrder = smaDcaStrategy.lastOrder.toLong()
        var time = System.currentTimeMillis()
        var dif = time - lastOrder

        println("process smaDcaStrategy")
        if (dif < dcaInterval) {
            println("delay ${dcaInterval - dif}")
            delay(dcaInterval - dif)
        }

        println("start to requesting kline")
        val kline = klineResponse(smaDcaStrategy)

        println("start to requesting sma")
        val sma = calculateSma(kline)
        println(sma)

        println("start to requesting price")
        val currentPrice =
            currentPrice(
                symbol = smaDcaStrategy.symbol,
                apiKey = smaDcaStrategy.apiKey,
                apiSecret = smaDcaStrategy.apiSecret,
            )
        println("lastPrice ${currentPrice.lastPrice}")

        if (sma < currentPrice.lastPrice.toDouble()) {
            placeOrder(smaDcaStrategy, Side.sell)
        } else {
            placeOrder(smaDcaStrategy, Side.buy)
        }

        println("update strategy")
        smaDcaStrategyRepository.updateSmaDcaStrategy(smaDcaStrategy, System.currentTimeMillis().toString())
        return true
    }

    suspend fun currentPrice(
        category: Category = Category.spot,
        symbol: String,
        apiKey: String,
        apiSecret: String,
    ): LastPrice {
        val queryString = "category=$category&symbol=$symbol"
        val response = BybitServer.get(apiKey, apiSecret, queryString, SuperInfo.GET_TICKET_ENDPOINT)

        println(response.bodyAsText())
        return json.decodeFromString<PriceResponse>(response.bodyAsText()).result.list[0]
    }

    suspend fun klineResponse(smaDcaStrategy: SmaDcaStrategy): Kline {
        val queryString =
            "category=${Category.spot}&symbol=${smaDcaStrategy.symbol}" +
                "&interval=${smaDcaStrategy.interval}&limit=${smaDcaStrategy.limit}"

        val response =
            BybitServer.get(
                smaDcaStrategy.apiKey,
                smaDcaStrategy.apiSecret,
                queryString,
                SuperInfo.GET_KLINE_ENDPOINT,
            )

        println(response.bodyAsText())
        return json.decodeFromString<KlineRes>(response.bodyAsText()).result
    }

    suspend fun placeOrder(
        smaDcaStrategy: SmaDcaStrategy,
        side: Side,
    ): Boolean {
        val order =
            Order(
                category = Category.spot,
                symbol = smaDcaStrategy.symbol,
                side = side,
                orderType = OrderType.market,
                qty = smaDcaStrategy.qta,
            )
//
//        val mp = mapOf(
//        "category" to "spot",
//        "symbol" to smaDcaStrategy.symbol,
//        "side" to side,
//        "orderType" to "Market",
//        "qty" to smaDcaStrategy.qta)

        val bodyString = json.encodeToString(order)

        val response =
            BybitServer.post(
                smaDcaStrategy.apiKey,
                smaDcaStrategy.apiSecret,
                bodyString,
                SuperInfo.CREATE_ORDER_ENDPOINT,
            )

        println(response.bodyAsText())
        return json.decodeFromString<RetMsg>(response.bodyAsText()).retMsg == "OK"
    }

    suspend fun calculateSma(kline: Kline): Double = kline.list.map { it[4].toDouble() }.average()

    suspend fun genSignature(apiSecret: String): String {
        val expires = System.currentTimeMillis() + 1000
        val signaturePayload = "GET/realtime$expires"

        return hmacSha256Hex(signaturePayload, apiSecret)
    }
}
