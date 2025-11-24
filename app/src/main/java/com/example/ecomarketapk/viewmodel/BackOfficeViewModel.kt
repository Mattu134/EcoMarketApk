package com.example.ecomarketapk.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomarketapk.data.ProductoRequest
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.data.toDomain
import com.example.ecomarketapk.model.Producto
import com.example.ecomarketapk.repository.ProductoRepository
import com.example.ecomarketapk.utils.crearImagenPartDesdeUri
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

data class ResultadoOperacion(val exito: Boolean, val mensaje: String)

class BackOfficeViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _inventario = MutableStateFlow<List<Producto>>(emptyList())
    val inventario: StateFlow<List<Producto>> = _inventario
    private val _productoEnEdicion = MutableStateFlow<Producto?>(null)
    val productoEnEdicion: StateFlow<Producto?> = _productoEnEdicion

    private val gson = Gson()

    fun cargarInventario() {
        viewModelScope.launch {
            try {
                val productosApi = repository.obtenerProductos()
                _inventario.value = productosApi.map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
                _inventario.value = emptyList()
            }
        }
    }

    fun cargarProductoParaEdicion(id: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.obtenerProductoPorId(id) // ProductoResponse
                _productoEnEdicion.value = resp.toDomain()
            } catch (e: Exception) {
                e.printStackTrace()
                _productoEnEdicion.value = null
            }
        }
    }

    suspend fun agregarProducto(
        context: Context,
        nombre: String,
        precio: String,
        descripcion: String,
        imagenUri: Uri?,
        categoria: String,
        stock: String,
        proveedor: String,
        lote: String,
        fechaExpiracion: String
    ): ResultadoOperacion {

        if (nombre.isBlank() || precio.isBlank() || descripcion.isBlank() ||
            categoria.isBlank() || stock.isBlank() || proveedor.isBlank() ||
            lote.isBlank() || fechaExpiracion.isBlank()
        ) {
            return ResultadoOperacion(false, "Por favor completa todos los campos")
        }

        if (imagenUri == null) {
            return ResultadoOperacion(false, "Debes seleccionar una imagen")
        }

        val precioInt = precio.toIntOrNull()
        val stockInt = stock.toIntOrNull()

        if (precioInt == null || precioInt <= 0) {
            return ResultadoOperacion(false, "Precio inválido")
        }
        if (stockInt == null || stockInt < 0) {
            return ResultadoOperacion(false, "Stock inválido")
        }

        return try {
            val request = ProductoRequest(
                nombre = nombre,
                descripcion = descripcion,
                precioClp = precioInt,
                stock = stockInt,
                categoria = categoria,
                proveedor = proveedor,
                numeroLote = lote,
                fechaExpiracion = fechaExpiracion
            )

            val json = gson.toJson(request)
            val productoBody = json.toRequestBody("application/json".toMediaType())
            val imagenPart = crearImagenPartDesdeUri(
                context = context,
                uri = imagenUri,
                nombreCampo = "imagen",
                nombreArchivo = "producto_${nombre}.jpg"
            )

            val creadoResponse = repository.crearProducto(
                productoJson = productoBody,
                imagen = imagenPart
            )

            val creado = creadoResponse.toDomain()
            _inventario.value = _inventario.value + creado

            ResultadoOperacion(true, "Producto guardado en el servidor")

        } catch (e: Exception) {
            e.printStackTrace()
            ResultadoOperacion(false, "Error al guardar: ${e.message}")
        }
    }

    suspend fun actualizarProducto(
        id: Long,
        nombre: String,
        precio: String,
        descripcion: String,
        categoria: String,
        stock: String,
        proveedor: String,
        lote: String,
        fechaExpiracion: String
    ): ResultadoOperacion {

        if (nombre.isBlank() || precio.isBlank() || descripcion.isBlank() ||
            categoria.isBlank() || stock.isBlank() || proveedor.isBlank() ||
            lote.isBlank() || fechaExpiracion.isBlank()
        ) {
            return ResultadoOperacion(false, "Por favor completa todos los campos")
        }

        val precioInt = precio.toIntOrNull()
        val stockInt = stock.toIntOrNull()

        if (precioInt == null || precioInt <= 0) {
            return ResultadoOperacion(false, "Precio inválido")
        }

        if (stockInt == null || stockInt < 0) {
            return ResultadoOperacion(false, "Stock inválido")
        }

        return try {
            val request = ProductoRequest(
                nombre = nombre,
                descripcion = descripcion,
                precioClp = precioInt,
                stock = stockInt,
                categoria = categoria,
                proveedor = proveedor,
                numeroLote = lote,
                fechaExpiracion = fechaExpiracion
            )

            val actualizadoResponse: ProductoResponse =
                repository.actualizarProducto(id, request)

            val actualizado = actualizadoResponse.toDomain()
            _inventario.value = _inventario.value.map {
                if (it.id == id) actualizado else it
            }

            ResultadoOperacion(true, "Producto actualizado correctamente")

        } catch (e: Exception) {
            e.printStackTrace()
            ResultadoOperacion(false, "Error al actualizar: ${e.message}")
        }
    }
}
