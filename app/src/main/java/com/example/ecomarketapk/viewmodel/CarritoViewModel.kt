package com.example.ecomarketapk.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.ecomarketapk.model.Producto

class CarritoViewModel : ViewModel() {

    val carrito = mutableStateMapOf<Producto, Int>()

    fun agregar(producto: Producto) {
        carrito[producto] = (carrito[producto] ?: 0) + 1
    }

    fun eliminar(producto: Producto) {
        val cantidadActual = carrito[producto]
        if (cantidadActual != null) {
            if (cantidadActual > 1) {
                carrito[producto] = cantidadActual - 1
            } else {
                carrito.remove(producto)
            }
        }
    }

    fun limpiar() {
        carrito.clear()
    }

    fun subtotal(): Double {
        return carrito.entries.sumOf { (producto, cantidad) -> producto.precio * cantidad }
    }
}
