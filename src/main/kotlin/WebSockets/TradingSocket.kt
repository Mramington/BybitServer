//package com.example.WebSockets
//
//import com.example.model.Strategy
//import io.ktor.client.HttpClient
//import io.ktor.client.engine.cio.CIO
//import io.ktor.client.plugins.websocket.webSocket
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import io.ktor.client.plugins.websocket.*
//import io.ktor.websocket.Frame
//import io.ktor.websocket.readText
//import io.ktor.websocket.send
//import io.netty.channel.socket.SocketChannel
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.delay
//import kotlinx.serialization.json.JsonPrimitive
//import javax.crypto.Mac
//import javax.crypto.spec.SecretKeySpec
//import kotlin.math.log
//
//class TradingSocket(
//    strategies: List<Strategy>,
//    privateUrl: String,
//    publicUrl: String,
//) {
//    private val Strategies: MutableList<Strategy> = strategies.toMutableList()
//    private val PrivateUrl: String = privateUrl
//    private val PublicUrl: String = publicUrl
//
//    init {
//
//    }
//
//
//    suspend fun AddStrategy(strategy: Strategy): Boolean
//    {
//        if (!strategy.actual)
//            return false
//
//        Strategies.add(strategy)
//    }
//
//
//    private suspend fun tradingWebsocket(strategy: Strategy) {
//        val scope = CoroutineScope(Dispatchers.Default)
//
//        val channel = Channel<String>(Channel.CONFLATED)
//        scope.launch {
//            publicSocket(strategy, channel)
//        }
//        scope.launch {
//            privateSocket(strategy, channel)
//        }
//    }
//
//
//
//    private suspend fun publicSocket(strategy: Strategy, channel: Channel<String>) {
//        val client = HttpClient(CIO) {
//            install(WebSockets)
//        }
//
//        client.webSocket(urlString = PublicUrl) {
//            launch {
//                while (true) {
//                    send(Frame.Text("""{"op":"ping"}"""))
//                    delay(20_000) // every 20 seconds
//                }
//            }
//
//            val ifTokenLong = strategy.ifTokenLong
//            val ifCostLong = strategy.ifCostLong
//
//
//            val subscribeRequest = buildSubscrive()
//            send(subscribeRequest)
//
//            println("Подписка на тикер отправлена")
//
//            // Чтение сообщений от сервера
//            for (frame in incoming) {
//                when (frame) {
//                    is Frame.Text -> println("📨 Ответ: ${frame.readText()}")
//                    else -> {}
//                }
//            }
//        }
//
//    }
//
//    private suspend fun privateSocket(strategy: Strategy, channel: Channel<String>) {
//        val client = HttpClient(CIO) {
//            install(WebSockets)
//        }
//
//
//        client.webSocket(urlString = PrivateUrl) {
//            launch {
//                while (true) {
//                    send(Frame.Text("""{"op":"ping"}"""))
//                    delay(20_000) // every 20 seconds
//                }
//            }
//
//            // authentication
//            send(buildAuthMessage())
//
//            // Чтение сообщений от сервера
//            for (frame in incoming) {
//                when (frame) {
//                    is Frame.Text -> println("📨 Ответ: ${frame.readText()}")
//                    else -> {}
//                }
//            }
//        }
//    }
//
//    suspend fun buildAuthMessage(apiKey: String, apiSecret: String) : String {
//        val expires = System.currentTimeMillis() + 1000
//
//        val signaturePayload = "GET/realtime${expires}"
//        val signature = hmacSha256Hex(signaturePayload, apiSecret)
//
//        val authRequest = BybitAuthRequest(
//            "10001",
//            "auth",
//            listOf(
//                JsonPrimitive(apiKey),
//                JsonPrimitive(expires.toString()),
//                JsonPrimitive(signature)
//            )
//        )
//
//        return json.encodeToString(authRequest)
//    }
//
//
//    private fun genSignature(data: String, key: String): String {
//        val algorithm = "HmacSHA256"
//        val mac = Mac.getInstance(algorithm)
//        val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm)
//        mac.init(secretKeySpec)
//        val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
//        return hmacBytes.joinToString("") { "%02x".format(it) }
//    }
//}
