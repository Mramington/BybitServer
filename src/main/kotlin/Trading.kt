package com.example

import com.example.HttpTrading.SmaDcaTrading.SmaDcaTrader
import com.example.model.SmaDcaStrategyRepository
import io.ktor.server.application.Application

fun Application.configureTrading(repository: SmaDcaStrategyRepository) {
    val smaDcaTrader = SmaDcaTrader(repository)
}
