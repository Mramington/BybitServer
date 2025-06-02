package com.example

import com.example.httpTrading.smaDcaTrading.SmaDcaTrader
import com.example.model.SmaDcaStrategyRepository
import io.ktor.server.application.Application

fun Application.configureTrading(repository: SmaDcaStrategyRepository) {
    val smaDcaTrader = SmaDcaTrader(repository)
}
