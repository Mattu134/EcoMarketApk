package com.example.ecomarketapk.data

import com.example.ecomarketapk.model.Producto
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

// Lo que el backend devuelve
data class ProductoResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    @SerializedName("precioClp")
    val precioClp: Int,
    val stock: Int,
    val categoria: String?,
    val proveedor: String?,
    @SerializedName("numeroLote")
    val numeroLote: String?,
    val fechaExpiracion: String?,
    val imagen: String?
)

// Lo que se envía al crear un producto con imagen
data class ProductoRequest(
    val nombre: String,
    val descripcion: String?,
    val precioClp: Int,
    val stock: Int,
    val categoria: String?,
    val proveedor: String?,
    val numeroLote: String?,
    val fechaExpiracion: String?
){
    fun toJson():String = Gson().toJson(this)
}

fun ProductoResponse.toDomain(): Producto =
    Producto(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        precioClp = precioClp,
        stock = stock,
        categoria = categoria,
        proveedor = proveedor,
        numeroLote = numeroLote,
        fechaExpiracion = fechaExpiracion,
        imagen = imagen
    )
