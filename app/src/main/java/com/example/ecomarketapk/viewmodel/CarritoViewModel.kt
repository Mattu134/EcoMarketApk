package com.example.ecomarketapk.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.data.StockRequest
import com.example.ecomarketapk.repository.ProductoRepository

class CarritoViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _carrito: SnapshotStateMap<ProductoResponse, Int> = mutableStateMapOf()
    val carrito: Map<ProductoResponse, Int> get() = _carrito

    private val _ultimaCompra = mutableStateOf<List<Pair<ProductoResponse, Int>>>(emptyList())
    val ultimaCompra: List<Pair<ProductoResponse, Int>> get() = _ultimaCompra.value

    fun agregar(producto: ProductoResponse) {
        val actual = _carrito[producto] ?: 0
        if (actual >= producto.stock) return
        _carrito[producto] = actual + 1
    }

    fun disminuir(producto: ProductoResponse) {
        val actual = _carrito[producto] ?: 0
        val nuevo = (actual - 1).coerceAtLeast(0)
        if (nuevo == 0) {
            _carrito.remove(producto)
        } else {
            _carrito[producto] = nuevo
        }
    }

    fun eliminarProducto(producto: ProductoResponse) {
        _carrito.remove(producto)
    }

    fun subtotalClp(): Double {
        return _carrito.entries.sumOf { entry ->
            entry.key.precioClp.toDouble() * entry.value
        }
    }

    fun totalUnidades(): Int = _carrito.values.sum()

    fun pagar(): Boolean {
        if (_carrito.isEmpty()) return false

        val haySinStock = _carrito.entries.any { (producto, cantidad) ->
            cantidad > producto.stock || producto.stock <= 0
        }

        if (haySinStock) return false
        return true
    }

    fun guardarUltimaCompra() {
        _ultimaCompra.value = _carrito.entries.map { it.toPair() }
    }

    fun limpiar() {
        _carrito.clear()
    }


    suspend fun procesarPagoYActualizarStock(): Boolean {
        if (!pagar()) return false

        return try {
            val items = _carrito.entries.map { (producto, cantidad) ->
                StockRequest(
                    productId = producto.id,
                    cantidadVendida = cantidad
                )
            }
            val actualizados = productoRepository.actualizarStock(items)

            actualizados.forEach { actualizado ->
                val entrada = _carrito.entries.find { it.key.id == actualizado.id }
                if (entrada != null) {
                    val cantidad = entrada.value
                    _carrito.remove(entrada.key)
                    _carrito[actualizado] = cantidad
                }
            }
            guardarUltimaCompra()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
