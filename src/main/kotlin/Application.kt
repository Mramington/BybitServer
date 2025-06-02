package com.example

import com.example.model.PostgresSmaDcaStrategyRepository
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = PostgresSmaDcaStrategyRepository()
    val config = Config(
        url = "jdbc:postgresql://localhost:5432/test",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "Gnim36343533",
    )

    configureDatabases(config)
    configureTrading(repository)
    configureRouting(repository)
}

object SuperInfo {
    const val TESTNET_URL = "https://api-testnet.bybit.com"
    const val CREATE_ORDER_ENDPOINT = "/v5/order/create"
    const val GET_TICKET_ENDPOINT = "/v5/market/tickers"
    const val GET_KLINE_ENDPOINT = "/v5/market/kline"
}
