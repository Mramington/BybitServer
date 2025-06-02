package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class SmaDcaStrategy (
    val userId: String,

    val apiKey: String,
    val apiSecret: String,
    var lastOrder: String,
    val dcaInterval: String,

    val qta: String,
    val symbol: String,
    val interval: String,
    val limit: String,
)