package com.example.httpTrading.smaDcaTrading.serialization

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
    Linear("linear"),
    Inverse("inverse"),
    Spot("spot"),
}

@Serializable
enum class Side(val side: String) {
    Buy("Buy"),
    Sell("Sell"),
}

@Serializable
enum class OrderType(val orderType: String) {
    Limit("Limit"),
    Market("Market"),
}
