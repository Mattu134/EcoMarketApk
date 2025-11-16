package com.example.ecomarketapk.model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val imagen: String?,
    val categoria: String?,
    val stock: Int,
    val proveedor: String?,
    val lote: String?,
    val fechaExpiracion: String?
)
