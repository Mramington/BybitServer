package com.example

import com.example.db.SmaDcaStrategyTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/test",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "Gnim36343533",
    )

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    transaction {
        SchemaUtils.create(SmaDcaStrategyTable)
    }
}
