package com.example.ecomarketapk.network

import com.example.ecomarketapk.data.ProductoRequest
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.data.StockRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductApi {

    @GET("api/products")
    suspend fun obtenerProductos(): List<ProductoResponse>

    @GET("api/products/{id}")
    suspend fun obtenerProductoPorId(
        @Path("id") id: Long
    ): ProductoResponse

    @Multipart
    @POST("api/products")
    suspend fun crearProducto(
        @Part("producto") productoJson: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): ProductoResponse

    @POST("api/products/actualizar-stock")
    suspend fun actualizarStock(
        @Body items: List<StockRequest>
    ): List<ProductoResponse>

    @PUT("api/products/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body datos: ProductoRequest
    ): ProductoResponse


    @DELETE("api/products/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    )
}
