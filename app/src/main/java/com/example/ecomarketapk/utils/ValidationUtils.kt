package com.example.ecomarketapk.utils

// Applying definitive fix for email validation regex.
object ValidationUtils {

    // Regex for email validation using a triple-quoted string to avoid escaping issues.
    // This avoids dependency on the Android framework in unit tests.
    private val EMAIL_ADDRESS_PATTERN = Regex(
        """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+"""
    )

    fun normalizeRut(rut: String): String {
        return rut.replace(Regex("[^0-9kK]"), "").uppercase()
    }

    fun isEmailValid(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_ADDRESS_PATTERN.matches(email)
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