package com.example.ecomarketapk.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {

    // https://api.frankfurter.app/latest?from=CLP&to=USD
    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String,
        @Query("to") to: String
    ): ExchangeRateResponse
}
