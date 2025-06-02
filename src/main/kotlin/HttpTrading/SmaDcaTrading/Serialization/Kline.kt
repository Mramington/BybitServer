package com.example.HttpTrading.SmaDcaTrading.Serialization

import kotlinx.serialization.Serializable

@Serializable
data class KlineRequest(
    val category: Category,
    val symbol: String,
    val interval: String,
    val limit: String,
)

@Serializable
data class KlineRes(
    val retCode: Int,
    val retMsg: String,
    val result: Kline,
)

@Serializable
data class Kline(
    val category: String,
    val symbol: String,
    val list: List<List<String>>,
)
