package com.example.ecomarketapk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomarketapk.model.Producto
import com.example.ecomarketapk.repository.ProductRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ResultadoAgregar(val exito: Boolean, val mensaje: String)

class BackOfficeViewModel : ViewModel() {

    private val _inventario = MutableStateFlow<List<Producto>>(emptyList())
    val inventario: StateFlow<List<Producto>> = _inventario

    fun cargarInventario(context: Context) {
        viewModelScope.launch {
            val lista = ProductRepository.obtenerProductos(context)
            _inventario.value = lista
        }
    }

    fun agregarProducto(
        context: Context,
        nombre: String,
        precio: String,
        descripcion: String,
        imagen: String,
        categoria: String,
        stock: String,
        proveedor: String,
        lote: String,
        fechaExpiracion: String
    ): ResultadoAgregar {

        if (nombre.isBlank() || precio.isBlank() || descripcion.isBlank() ||
            imagen.isBlank() || categoria.isBlank() ||
            stock.isBlank() || proveedor.isBlank() || lote.isBlank() || fechaExpiracion.isBlank()
        ) {
            return ResultadoAgregar(false, "Por favor completa todos los campos")
        }

        val precioDouble = precio.toDoubleOrNull()
        if (precioDouble == null || precioDouble <= 0) {
            return ResultadoAgregar(false, "Precio inválido")
        }

        val stockInt = stock.toIntOrNull()
        if (stockInt == null || stockInt < 0) {
            return ResultadoAgregar(false, "Stock inválido")
        }

        val nuevoId = (_inventario.value.maxOfOrNull { it.id } ?: 0) + 1

        val nuevoProducto = Producto(
            id = nuevoId,
            nombre = nombre,
            descripcion = descripcion,
            precio = precioDouble,
            imagen = imagen,
            categoria = categoria,
            stock = stockInt,
            proveedor = proveedor,
            lote = lote,
            fechaExpiracion = fechaExpiracion
        )

        val listaActualizada = _inventario.value + nuevoProducto
        _inventario.value = listaActualizada

        guardarInventarioEnArchivo(context, listaActualizada)

        return ResultadoAgregar(true, "Producto agregado correctamente")
    }

    private fun guardarInventarioEnArchivo(context: Context, inventario: List<Producto>) {
        val gson = Gson()
        val json = gson.toJson(inventario)

        val file = File(context.filesDir, "productos_actualizados.json")
        file.writeText(json)
    }
}
