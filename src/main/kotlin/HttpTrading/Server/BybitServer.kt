package com.example.HttpTrading.Server

import com.example.HttpTrading.Encryption.hmacSha256Hex
import com.example.SuperInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

object BybitServer : Server {
    private var client = HttpClient(CIO)
    private val url = SuperInfo.TESTNET_URL

    override suspend fun post(
        apiKey: String,
        apiSecret: String,
        bodyString: String,
        endpoint: String,
    ): io.ktor.client.statement.HttpResponse {
        val timestamp = System.currentTimeMillis().toString()
        val recvWindow = "5000"
        val preSign = timestamp + apiKey + recvWindow + bodyString
        val sign = hmacSha256Hex(preSign, apiSecret)

        val response =
            client.post(url + endpoint) {
                contentType(ContentType.Application.Json)
                setBody(bodyString)
                headers.append("X-BAPI-API-KEY", apiKey)
                headers.append("X-BAPI-TIMESTAMP", timestamp)
                headers.append("X-BAPI-SIGN", sign)
                headers.append("X-BAPI-RECV-WINDOW", recvWindow)
            }

        println(response.bodyAsText())
        return response
    }

    override suspend fun get(
        apiKey: String,
        apiSecret: String,
        queryString: String,
        endpoint: String,
    ): io.ktor.client.statement.HttpResponse {
        val timestamp = System.currentTimeMillis().toString()
        val recvWindow = "5000"
        val preSign = timestamp + apiKey + recvWindow + queryString
        val sign = hmacSha256Hex(preSign, apiSecret)

        val response =
            client.get("$url$endpoint?$queryString") {
                contentType(ContentType.Application.Json)
                headers {
                    append("X-BYBIT-API-KEY", apiKey)
                    append("X-BYBIT-TIMESTAMP", timestamp)
                    append("X-BYBIT-SIGN", sign)
                    append("X-BYBIT-RECV-WINDOW", recvWindow)
                }
            }

        println(response.bodyAsText())
        return response
    }
}
