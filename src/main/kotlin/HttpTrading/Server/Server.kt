package com.example.HttpTrading.Server

import com.example.model.PostgresSmaDcaStrategyRepository
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.server.application.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.http.ContentType
import com.example.HttpTrading.Encryption.hmacSha256Hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.example.model.SmaDcaStrategy
import com.example.model.SmaDcaStrategyRepository
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
import java.sql.Connection
import java.sql.DriverManager
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import org.jetbrains.exposed.sql.*

interface Server {
    suspend fun post(
        apiKey: String,
        apiSecret: String,
        bodyString: String,
        endpoint: String
    ): io.ktor.client.statement.HttpResponse

    suspend fun get(
        apiKey: String,
        apiSecret: String,
        bodyString: String,
        endpoint: String
    ): io.ktor.client.statement.HttpResponse
}
