package com.example.ecomarketapk.network

import com.example.ecomarketapk.data.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {

    @GET("v6/c9f1a3c3591e1530a066e521/latest/{base}")
    suspend fun getLatestRates(
        @Path("base") base: String = "CLP"
    ): ExchangeRateResponse
}
