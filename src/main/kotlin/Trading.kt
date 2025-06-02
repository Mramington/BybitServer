package com.example

import io.ktor.server.application.Application
import com.example.HttpTrading.SmaDcaTrading.SmaDcaTrader
import com.example.model.SmaDcaStrategyRepository

fun Application.configureTrading(repository: SmaDcaStrategyRepository) {
    val smaDcaTrader = SmaDcaTrader(repository)
}
