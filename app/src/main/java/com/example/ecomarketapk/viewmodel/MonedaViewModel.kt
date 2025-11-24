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

    private val api = ExchangeRateClient.api

    fun cargarTasaClpUsd() {
        viewModelScope.launch {
            try {
                Log.d("MonedaViewModel", "Solicitando tasa USD → CLP (Frankfurter)...")

                val response = api.getLatestRates(
                    from = "USD",
                    to = "CLP"
                )

                Log.d(
                    "MonedaViewModel",
                    "Respuesta Frankfurter: base=${response.base}, rates=${response.rates}"
                )

                val usdToClp = response.rates["CLP"]

                if (usdToClp != null && usdToClp > 0) {
                    val clpToUsd = 1.0 / usdToClp
                    _tasaClpUsd.value = clpToUsd
                    Log.d("MonedaViewModel", "Tasa CLP→USD cargada: $clpToUsd")
                } else {
                    Log.e("MonedaViewModel", "No se encontró 'CLP' en rates: ${response.rates}")
                    _tasaClpUsd.value = null
                }
            } catch (e: Exception) {
                Log.e("MonedaViewModel", "Error cargando tasa CLP→USD", e)
                _tasaClpUsd.value = null
            }
        }
    }
}

