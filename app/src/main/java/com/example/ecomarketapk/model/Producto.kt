package com.example.ecomarketapk.model

data class Producto(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precioClp: Int,
    val stock: Int,
    val categoria: String?,
    val proveedor: String?,
    val numeroLote: String?,
    val fechaExpiracion: String?,
    val imagen: String?
)
