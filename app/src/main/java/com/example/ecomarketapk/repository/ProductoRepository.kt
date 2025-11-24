package com.example.ecomarketapk.repository

import com.example.ecomarketapk.data.ProductoRequest
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.data.StockRequest
import com.example.ecomarketapk.network.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductoRepository {

    private val api = ApiClient.productApi

    suspend fun obtenerProductoPorId(id: Long): ProductoResponse {
        return ApiClient.productApi.obtenerProductoPorId(id)
    }

    suspend fun obtenerProductos(): List<ProductoResponse> =
        api.obtenerProductos()

    suspend fun crearProducto(
        productoJson: RequestBody,
        imagen: MultipartBody.Part
    ): ProductoResponse =
        api.crearProducto(productoJson, imagen)
    suspend fun actualizarProducto(id: Long, request: ProductoRequest): ProductoResponse {
        return ApiClient.productApi.actualizarProducto(id, request)
    }

    suspend fun actualizarStock(items: List<StockRequest>): List<ProductoResponse> {
        return api.actualizarStock(items)
    }
}
