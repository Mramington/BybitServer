package com.example

import com.example.WebSockets.configureSockets
import com.example.model.PostgresSmaDcaStrategyRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    configureSockets()
    val repository = PostgresSmaDcaStrategyRepository()

    configureSerialization(repository)
    configureDatabases()
    configureRouting()
}


object SuperInfo {
    const val testnetUrl = "https://api-testnet.bybit.com"
    const val createOrderEndpoint = "/v5/order/create"
    const val getTicketEndpoint = "/v5/market/tickers"
    const val getKlineEndpoint = "/v5/market/kline"
}