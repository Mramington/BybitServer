package com.example.HttpTrading.Server

import io.ktor.client.request.get
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.jetbrains.exposed.sql.*

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
        bodyString: String,
        endpoint: String,
    ): io.ktor.client.statement.HttpResponse
}
