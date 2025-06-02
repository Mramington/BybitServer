package com.example.WebSockets

import com.example.model.Strategy
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


import io.ktor.client.HttpClient
import io.ktor.server.application.*
import io.ktor.server.routing.*
//import io.ktor.server.websocket.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlin.time.Duration.Companion.seconds
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import io.ktor.server.websocket.*

fun Application.configureSockets() = runBlocking {
    launch {
        launchWebSocketBybit()
    }
    launch {
        launchWebSocketBybitPublicSpot()
    }




//    install(io.ktor.server.websocket.WebSockets) {
//        pingPeriod = 15.seconds
//        timeout = 15.seconds
//        maxFrameSize = Long.MAX_VALUE
//        masking = false
//    }
//    routing {
//        webSocket("/ws") { // websocketSession
//            for (frame in incoming) {
//                if (frame is Frame.Text) {
//                    val text = frame.readText()
//                    try {
//                        val strategy = json.decodeFromString<Strategy>(text)
//
//                    }
//                    send(Frame.Text("YOU SAID: $text"))
//                    if (text.equals("bye", ignoreCase = true)) {
//                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
//                    }
//                }
//            }
//        }
//    }
}


val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

@Serializable
data class BybitTickerMessage(
    val topic: String,
    val type: String,
    val data: TickerData? = null
)

@Serializable
data class TickerData(
    val symbol: String,
    val lastPrice: String,
    val indexPrice: String,
    val markPrice: String
)


@Serializable
data class BybitSubscribeRequest(
    val op: String,
    val args: List<String>
)


@Serializable
data class BybitAuthRequest(
    val req_id: String,
    val op: String,
    val args: List<JsonElement>
)



suspend fun buildAuthMessage() : String {
    val expires = System.currentTimeMillis() + 1000
    val apiKey = "K0r7bIYGfh8cdYL01a"
    val apiSecret = "iwwksVOJOzx6lejC7vNe1sYWaKLaL6NdnM65"

    val signaturePayload = "GET/realtime${expires}"
    val signature = hmacSha256Hex(signaturePayload, apiSecret)

    val authRequest = BybitAuthRequest("10001", "auth", listOf(
        JsonPrimitive(apiKey),
        JsonPrimitive(expires.toString()),
        JsonPrimitive(signature)
    ))

    return json.encodeToString(authRequest)
}

suspend fun buildSubscrive() : String {
    val subscribeRequest = BybitSubscribeRequest("subscribe", listOf("tickers.BTCUSDT"))

    return json.encodeToString(subscribeRequest)
}


suspend fun Application.launchWebSocketBybitPublicSpot() {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    val url = "wss://stream-testnet.bybit.com/v5/public/spot"

    client.webSocket(urlString = url) {
        println("Подключили url")

        launch {
            while (true) {
                send(Frame.Text("""{"op":"ping"}"""))
                delay(20_000) // каждые 20 секунд
            }
        }

        val subscribeRequest = buildSubscrive()
        send(subscribeRequest)

        println("Подписка на тикер отправлена")

        // Чтение сообщений от сервера
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> println("📨 Ответ: ${frame.readText()}")
                else -> {}
            }
        }
    }

}


suspend fun Application.launchWebSocketBybit() {
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    val url = "wss://stream-testnet.bybit.com/v5/private"

    client.webSocket(urlString = url) {
        println("Подключили url")

        launch {
            while (true) {
                send(Frame.Text("""{"op":"ping"}"""))
                delay(20_000) // каждые 20 секунд
            }
        }

        val authentication = buildAuthMessage()
        send(authentication)

        println("Подписка на тикер отправлена")

        // Чтение сообщений от сервера
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> println("📨 Ответ: ${frame.readText()}")
                else -> {}
            }
        }
    }
}

fun hmacSha256Hex(data: String, key: String): String {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm)
    mac.init(secretKeySpec)
    val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
    return hmacBytes.joinToString("") { "%02x".format(it) }
}