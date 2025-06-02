package com.example.HttpTrading.SmaDcaTrading

import com.example.model.Category
import kotlinx.serialization.Serializable

@Serializable
data class KlineRequest(
    val category: Category,
    val symbol: String,
    val interval: String,
    val limit: String,
)

@Serializable
data class Kline (
    val category: String,
    val symbol: String,
    val klineList: List<KlineItem>,
)

@Serializable
data class KlineItem (
    val startTime: String,
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val closePrice: String,
    val volume: String,
    val turnover: String,
)