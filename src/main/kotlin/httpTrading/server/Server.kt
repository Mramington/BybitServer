package com.example.httpTrading.server

interface Server {
    suspend fun post(
        apiKey: String,
        apiSecret: String,
        bodyString: String,
        endpoint: String,
    ): io.ktor.client.statement.HttpResponse

    suspend fun get(
        apiKey: String,
        apiSecret: String,
        queryString: String,
        endpoint: String,
    ): io.ktor.client.statement.HttpResponse
}
