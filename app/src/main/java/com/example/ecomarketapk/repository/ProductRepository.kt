package com.example.ecomarketapk.repository

import android.content.Context
import com.example.ecomarketapk.model.Producto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ProductRepository {

    fun obtenerProductos(
        context: Context,
        filename: String = "productos.json"
    ): List<Producto> {
        return try {
            val json = context.assets.open(filename).bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Producto>>() {}.type
            Gson().fromJson<List<Producto>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
