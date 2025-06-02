package com.example.HttpTrading.SmaDcaTrading.Serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PriceResponse(
    val retCode: Int,
    val retMsg: String,
    val result: PriseResult,
    val retExtInfo: JsonElement,
    val time: Long,
)

@Serializable
data class PriseResult(
    val category: String,
    val list: List<LastPrice>,
)

@Serializable
data class LastPrice(
    val symbol: String,
    val bid1Price: String,
    val bid1Size: String,
    val ask1Price: String,
    val ask1Size: String,
    val lastPrice: String,
    val prevPrice24h: String,
    val price24hPcnt: String,
    val highPrice24h: String,
    val lowPrice24h: String,
    val turnover24h: String,
    val volume24h: String,
    val usdIndexPrice: String,
)
