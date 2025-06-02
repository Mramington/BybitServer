package com.example.db

import com.example.model.SmaDcaStrategy
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object SmaDcaStrategyTable : IntIdTable("smaDcaStrategy") {
    val userId = varchar("userId", 50)

    val apiKey = varchar("apiKey", 50)
    val apiSecret = varchar("apiSecret", 50)
    var lastOrder = varchar("lastOrder", 50)
    val dcaInterval = varchar("dcaInterval", 50)

    val qta = varchar("qta", 50)
    val symbol = varchar("symbol", 50)
    val interval = varchar("interval", 50)
    val limit = varchar("limit", 50)
}

class SmaDcaStrategyDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SmaDcaStrategyDAO>(SmaDcaStrategyTable)

    var userId by SmaDcaStrategyTable.userId

    var apiKey by SmaDcaStrategyTable.apiKey
    var apiSecret by SmaDcaStrategyTable.apiSecret
    var lastOrder by SmaDcaStrategyTable.lastOrder
    var dcaInterval by SmaDcaStrategyTable.dcaInterval

    var qta by SmaDcaStrategyTable.qta
    var symbol by SmaDcaStrategyTable.symbol
    var interval by SmaDcaStrategyTable.interval
    var limit by SmaDcaStrategyTable.limit
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T = newSuspendedTransaction(Dispatchers.IO, statement = block)

fun sDdaoToModel(dao: SmaDcaStrategyDAO) =
    SmaDcaStrategy(
        dao.userId,
        dao.apiKey,
        dao.apiSecret,
        dao.lastOrder,
        dao.dcaInterval,
        dao.qta,
        dao.symbol,
        dao.interval,
        dao.limit,
    )
