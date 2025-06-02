package com.example

import com.example.model.PostgresSmaDcaStrategyRepository
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = PostgresSmaDcaStrategyRepository()

    println("start app")
    configureDatabases()
    configureTrading(repository)
    configureRouting(repository)
}

object SuperInfo {
    const val testnetUrl = "https://api-testnet.bybit.com"
    const val createOrderEndpoint = "/v5/order/create"
    const val getTicketEndpoint = "/v5/market/tickers"
    const val getKlineEndpoint = "/v5/market/kline"
}
