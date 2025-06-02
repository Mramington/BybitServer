package com.example

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import com.example.HttpTrading.SmaDcaTrading.SmaDcaTrader
import com.example.model.SmaDcaStrategyRepository
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Application.configureTrading(repository: SmaDcaStrategyRepository) {
    CoroutineScope(Dispatchers.Default).launch {
        smaDcaTrader = SmaDcaTrader(repository)
    }
}