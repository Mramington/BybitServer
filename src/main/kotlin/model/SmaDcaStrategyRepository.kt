package com.example.model

import com.example.db.SmaDcaStrategyDAO

interface SmaDcaStrategyRepository {
    suspend fun allSmaDcaStrategies(): List<SmaDcaStrategy>
    suspend fun removeSmaDcaStrategy(userId: String): Boolean
    suspend fun addSmaDcaStrategy(smaDcaStrategy: SmaDcaStrategy): SmaDcaStrategyDAO
    suspend fun smaDcaStrategyByUserId(userId: String): SmaDcaStrategy?
    suspend fun updateSmaDcaStrategy(smaDcaStrategy: SmaDcaStrategy, lastOrder: String)
}