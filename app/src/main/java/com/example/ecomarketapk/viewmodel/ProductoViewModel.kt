package com.example.ecomarketapk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomarketapk.data.ProductoRequest
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.repository.ProductoRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoResponse>>(emptyList())
    val productos: StateFlow<List<ProductoResponse>> = _productos

    private val gson = Gson()

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                _productos.value = repository.obtenerProductos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun crearProductoConImagen(
        productoRequest: ProductoRequest,
        imagenPart: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val json = gson.toJson(productoRequest)
                val requestBody = RequestBody.create(
                    "application/json".toMediaType(),
                    json
                )
                val creado = repository.crearProducto(
                    productoJson = requestBody,
                    imagen = imagenPart
                )
                _productos.value = _productos.value + creado

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
