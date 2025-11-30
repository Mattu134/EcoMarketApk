package com.example.ecomarketapk.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomarketapk.network.ExchangeRateClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MonedaViewModel : ViewModel() {

    private val _tasaClpUsd = MutableStateFlow<Double?>(null)
    val tasaClpUsd: StateFlow<Double?> = _tasaClpUsd

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarTasaClpUsd() {
        viewModelScope.launch {
            try {
                Log.d("MonedaViewModel", "Solicitando tasa CLP → USD (ExchangeRate-API)...")

                val response = ExchangeRateClient.api.getLatestRates()
                val usdRate = response.conversionRates["USD"]
                if (usdRate != null) {
                    _tasaClpUsd.value = usdRate
                    _error.value = null
                    Log.d("MonedaViewModel", "Tasa CLP→USD recibida: $usdRate")
                } else {
                    _error.value = "No se encontró la tasa CLP→USD"
                    Log.e("MonedaViewModel", "USD no está en conversionRates")
                }
            } catch (e: Exception) {
                Log.e("MonedaViewModel", "Error cargando tasa CLP→USD", e)
                _error.value = "Error al cargar tasa CLP→USD"
            }
        }
    }
}
