package com.example

import com.example.db.SmaDcaStrategyTable
import io.ktor.http.Url
import io.ktor.http.hostIsIp
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.Driver

fun Application.configureDatabases(config: Config) {
    Database.connect(
        url = config.url,
        driver = config.driver,
        user = config.user,
        password = config.password,
    )

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    transaction {
        SchemaUtils.create(SmaDcaStrategyTable)
    }
}

data class Config(
    val url: String,
    val driver: String,
    val user: String,
    val password: String,
)