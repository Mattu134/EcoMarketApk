package com.example.ecomarketapk.utils

object ValidationUtils {

    private val EMAIL_ADDRESS_PATTERN = Regex(
        """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+"""
    )

    fun isEmailValid(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_ADDRESS_PATTERN.matches(email)
    }

    fun normalizeRut(rut: String): String {
        return rut.replace(Regex("[^0-9kK]"), "").uppercase()
    }

    fun isRutValid(rut: String): Boolean {
        val rutLimpio = normalizeRut(rut)
        if (rutLimpio.length !in 8..9) return false

        val cuerpo = rutLimpio.dropLast(1)
        val dvIngresado = rutLimpio.last()

        if (!cuerpo.all { it.isDigit() }) return false
        if (dvIngresado != 'K' && !dvIngresado.isDigit()) return false

        var suma = 0
        var multiplicador = 2

        for (c in cuerpo.reversed()) {
            suma += c.digitToInt() * multiplicador
            multiplicador++
            if (multiplicador == 8) multiplicador = 2
        }

        val dvCalculado = when (val resto = 11 - (suma % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> resto.toString().first()
        }

        return dvIngresado == dvCalculado
    }
}
