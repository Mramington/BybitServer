package com.example.HttpTrading.SmaDcaTrading

import com.example.SuperInfo
import com.example.WebSockets.hmacSha256Hex
import com.example.model.Category
import com.example.model.Order
import com.example.model.OrderType
import com.example.model.Side
import com.example.model.SmaDcaStrategy
import com.example.model.SmaDcaStrategyRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.util.StringValuesImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

class SmaDcaTrader(
    private val smaDcaStrategyRepository: SmaDcaStrategyRepository,
    private var smaDcaStrategies: MutableList<SmaDcaStrategy>,
    private val url: String,
) {
    private val json = Json { encodeDefaults = true }
    private val client = HttpClient(CIO)


    init {
        CoroutineScope(Dispatchers.Default).launch {
            smaDcaStrategyRepository.allSmaDcaStrategies().forEach {
                launch {
                    processSmaDcaStrategy(it)
                }
            }
        }
    }

    suspend fun processSmaDcaStrategy(smaDcaStrategy: SmaDcaStrategy) {
        val dcaInterval = smaDcaStrategy.dcaInterval.toLong()
        var lastOrder = smaDcaStrategy.lastOrder.toLong()
        var time = System.currentTimeMillis()
        var dif = time - lastOrder

        if (dcaInterval < dif)
            delay(dif - dcaInterval)

        val kline = klineResponse(smaDcaStrategy)
        val sma = calculateSma(kline)
        val currentPrice = currentPrice(
            symbol = smaDcaStrategy.symbol,
            apiKey = smaDcaStrategy.apiKey,
            apiSecret = smaDcaStrategy.apiSecret
        )

        if (sma < currentPrice.lastPrice.toDouble())
            placeOrder(smaDcaStrategy, Side.sell)
        else
            placeOrder(smaDcaStrategy, Side.buy)

        smaDcaStrategyRepository.updateSmaDcaStrategy(smaDcaStrategy, System.currentTimeMillis().toString())
    }

    suspend fun currentPrice(
        category: Category = Category.spot,
        symbol: String,
        apiKey: String,
        apiSecret: String
    ): LastPrice {
        val priceRequest = PriceRequest(
            category = category,
            symbol = symbol,
        )

        val bodyString = json.encodeToString(priceRequest)
        val response = processRequest(apiKey, apiSecret, bodyString, SuperInfo.getTicketEndpoint)

        return json.decodeFromString<LastPrice>(response.bodyAsText())
    }

    suspend fun klineResponse(smaDcaStrategy: SmaDcaStrategy): Kline {
        val klineRequest = KlineRequest(
            category = Category.spot,
            symbol = smaDcaStrategy.symbol,
            interval = smaDcaStrategy.interval,
            limit = smaDcaStrategy.limit,
        )

        val bodyString = json.encodeToString(klineRequest)

        val response = processRequest(
            smaDcaStrategy.apiKey,
            smaDcaStrategy.apiSecret,
            bodyString,
            SuperInfo.getKlineEndpoint
        )

        return json.decodeFromString<Kline>(response.bodyAsText())
    }

    suspend fun placeOrder(smaDcaStrategy: SmaDcaStrategy, side: Side): Boolean {
        val order = Order(
            category = Category.spot,
            symbol = smaDcaStrategy.symbol,
            side = side,
            orderType = OrderType.market,
            qty = smaDcaStrategy.qta
        )

        val bodyString = json.encodeToString(order)
        val response = processRequest(
            smaDcaStrategy.apiKey,
            smaDcaStrategy.apiSecret,
            bodyString,
            SuperInfo.createOrderEndpoint
        )

        return json.decodeFromString<RetMsg>(response.bodyAsText()).retMsg == "OK"
    }

    suspend fun processRequest(apiKey: String, apiSecret: String, bodyString: String, endpoint: String): HttpResponse {
        val timestamp = System.currentTimeMillis().toString()
        val recvWindow = "5000"
        val preSign = apiKey + timestamp + recvWindow + bodyString
        val sign = hmacSha256Hex(preSign, apiSecret)

        val response = client.post(url + endpoint) {
            contentType(ContentType.Application.Json)
            setBody(bodyString)
            headers {
                append("X-BYBIT-API-KEY", apiKey)
                append("X-BYBIT-TIMESTAMP", timestamp)
                append("X-BYBIT-SIGN", sign)
                append("X-BYBIT-RECV-WINDOW", recvWindow)
            }
        }

        return response
    }

    suspend fun calculateSma(kline: Kline): Double = kline.klineList.map { it.closePrice.toDouble() }.average()


    suspend fun genSignature(apiSecret: String): String {
        val expires = System.currentTimeMillis() + 1000
        val signaturePayload = "GET/realtime${expires}"

        return hmacSha256Hex(signaturePayload, apiSecret)
    }
}

@Serializable
data class PriceRequest(
    val category: Category,
    val symbol: String,
)

@Serializable
data class RetMsg(
    val retMsg: String,
)

@Serializable
data class LastPrice(
    val lastPrice: String,
)