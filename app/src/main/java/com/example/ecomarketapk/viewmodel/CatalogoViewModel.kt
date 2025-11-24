package com.example.ecomarketapk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<ProductoResponse>>(emptyList())
    val productos: StateFlow<List<ProductoResponse>> = _productos

    private val _productosFiltrados = MutableStateFlow<List<ProductoResponse>>(emptyList())
    val productosFiltrados: StateFlow<List<ProductoResponse>> = _productosFiltrados

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _categorias = MutableStateFlow<List<String>>(emptyList())
    val categorias: StateFlow<List<String>> = _categorias

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val lista = repository.obtenerProductos()
                _productos.value = lista
                _categorias.value = lista.mapNotNull { it.categoria }.distinct()
                actualizarFiltro()
            } finally {
                _loading.value = false
            }
        }
    }

    fun onSearchChange(text: String) {
        _searchQuery.value = text
        actualizarFiltro()
    }

    fun onCategoriaSeleccionada(categoria: String?) {
        _categoriaSeleccionada.value = categoria
        actualizarFiltro()
    }

    private fun actualizarFiltro() {
        val q = _searchQuery.value.trim().lowercase()
        val cat = _categoriaSeleccionada.value

        _productosFiltrados.value = _productos.value.filter { p ->
            (cat == null || p.categoria == cat) &&
                    (q.isEmpty() || p.nombre.lowercase().contains(q))
        }
    }
}
