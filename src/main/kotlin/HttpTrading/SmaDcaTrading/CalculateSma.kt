package com.example.HttpTrading.SmaDcaTrading

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import com.example.WebSockets.BybitAuthRequest
import com.example.WebSockets.hmacSha256Hex
import com.example.WebSockets.json
import com.example.model.Category
import com.example.model.Order
import com.example.model.OrderType
import com.example.model.Side
import com.example.model.SmaDcaStrategy
import kotlinx.serialization.json.JsonPrimitive

fun hmacSha256Hex(data: String, key: String): String {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm)
    mac.init(secretKeySpec)
    val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
    return hmacBytes.joinToString("") { "%02x".format(it) }
}
