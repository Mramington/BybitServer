package com.example.HttpTrading.SmaDcaTrading.Serialization

import kotlinx.serialization.Serializable


@Serializable
data class Order(
    val category: Category,
    val symbol: String,
    val side: Side,
    val orderType: OrderType,
    val qty: String,
)

@Serializable
enum class Category(val category: String) {
    linear("linear"),
    inverse("inverse"),
    spot("spot"),
}

@Serializable
enum class Side(val side: String) {
    buy("Buy"),
    sell("Sell"),
}

@Serializable
enum class OrderType(val orderType: String) {
    limit("Limit"),
    market("Market"),
}

