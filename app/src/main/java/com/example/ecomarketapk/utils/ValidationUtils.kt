package com.example.ecomarketapk.utils
import android.util.Patterns

object ValidationUtils {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validación de RUT
    fun isRutValid(rut: String): Boolean {
        val rutLimpio = rut.replace(Regex("[.-]"), "").uppercase()
        if (rutLimpio.length !in 8..9) return false

        val cuerpo = rutLimpio.dropLast(1)
        val dv = rutLimpio.last()

        if (!cuerpo.all { it.isDigit() }) return false
        if (dv != 'K' && !dv.isDigit()) return false

        try {
            var suma = 0
            var multiplicador = 2
            for (i in cuerpo.reversed()) {
                suma += i.toString().toInt() * multiplicador
                multiplicador++
                if (multiplicador == 8) multiplicador = 2
            }
            val dvCalculado = when (val resto = 11 - (suma % 11)) {
                11 -> '0'
                10 -> 'K'
                else -> resto.toString().first()
            }
            return dv == dvCalculado
        } catch (e: Exception) {
            return false
        }
    }
}