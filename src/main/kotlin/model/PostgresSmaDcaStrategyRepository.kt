package com.example.model

import com.example.db.SmaDcaStrategyDAO
import com.example.db.SmaDcaStrategyTable
import com.example.db.sDdaoToModel
import com.example.db.suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class PostgresSmaDcaStrategyRepository : SmaDcaStrategyRepository {
    override suspend fun allSmaDcaStrategies(): List<SmaDcaStrategy> =
        suspendTransaction {
            SmaDcaStrategyDAO.all().map(::sDdaoToModel)
        }

    override suspend fun removeSmaDcaStrategy(userId: String): Boolean =
        suspendTransaction {
            val deleted =
                SmaDcaStrategyTable.deleteWhere {
                    SmaDcaStrategyTable.userId eq userId
                }
            deleted == 1
        }

    override suspend fun addSmaDcaStrategy(smaDcaStrategy: SmaDcaStrategy) =
        suspendTransaction {
            SmaDcaStrategyDAO.new {
                userId = smaDcaStrategy.userId

                apiKey = smaDcaStrategy.apiKey
                apiSecret = smaDcaStrategy.apiSecret
                lastOrder = smaDcaStrategy.lastOrder
                dcaInterval = smaDcaStrategy.dcaInterval

                qta = smaDcaStrategy.qta
                symbol = smaDcaStrategy.symbol
                interval = smaDcaStrategy.interval
                limit = smaDcaStrategy.limit
            }
        }

    override suspend fun smaDcaStrategyByUserId(userId: String): SmaDcaStrategy? =
        suspendTransaction {
            SmaDcaStrategyDAO
                .find { (SmaDcaStrategyTable.userId eq userId) }
                .limit(1)
                .map(::sDdaoToModel)
                .firstOrNull()
        }

    override suspend fun allSmaDcaStrategyByUserId(userId: String): List<SmaDcaStrategy> =
        suspendTransaction {
            SmaDcaStrategyDAO
                .find { (SmaDcaStrategyTable.userId eq userId) }
                .map(::sDdaoToModel)
                .toList()
        }

    override suspend fun updateSmaDcaStrategy(
        smaDcaStrategy: SmaDcaStrategy,
        lastOrder: String,
    ) = suspendTransaction {
        SmaDcaStrategyTable.update(where = {
            (SmaDcaStrategyTable.interval eq smaDcaStrategy.interval) and
                (SmaDcaStrategyTable.limit eq smaDcaStrategy.limit) and
                (SmaDcaStrategyTable.symbol eq smaDcaStrategy.symbol) and
                (SmaDcaStrategyTable.userId eq smaDcaStrategy.userId)
        }) {
            it[this.lastOrder] = lastOrder
        }
    }
}
