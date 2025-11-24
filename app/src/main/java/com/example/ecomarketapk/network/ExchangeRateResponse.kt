package com.example.ecomarketapk.network

data class ExchangeRateResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
