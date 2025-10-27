package com.example.ecomarketapk.model

data class Usuario(
    val nombre: String,
    val email: String,
    val direccion: String,
    val rut: String,
    val password: String,
    val rol: String
)