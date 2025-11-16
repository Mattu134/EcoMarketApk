package com.example.ecomarketapk.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import com.example.ecomarketapk.model.Producto

class CarritoViewModel : ViewModel() {

    private val _carrito: SnapshotStateMap<Producto, Int> = mutableStateMapOf()
    val carrito: Map<Producto, Int> get() = _carrito

    fun agregar(producto: Producto) {
        val actual = _carrito[producto] ?: 0
        _carrito[producto] = actual + 1
    }

    fun disminuir(producto: Producto) {
        val actual = _carrito[producto] ?: 0
        if (actual <= 1) {
            _carrito.remove(producto)
        } else {
            _carrito[producto] = actual - 1
        }
    }

    fun eliminarProducto(producto: Producto) {
        _carrito.remove(producto)
    }

    fun subtotal(): Double {
        return _carrito.entries.sumOf { it.key.precio * it.value }
    }

    fun pagar(): Boolean {
        return (0..100).random() < 50
    }
    fun limpiar(){
        _carrito.clear()
    }
}
