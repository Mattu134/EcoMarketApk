package com.example.ecomarketapk.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.ecomarketapk.model.Usuario
import com.example.ecomarketapk.repository.UserRepository
import com.example.ecomarketapk.utils.ValidationUtils

class AuthViewModel : ViewModel() {
    var mensaje = mutableStateOf("")
    var usuarioActual = mutableStateOf<Usuario?>(null)

    fun registrar(
        context: Context,
        nombre: String,
        email: String,
        direccion: String,
        rut: String,
        password: String
    ): Boolean {
        val nombreTrim = nombre.trim()
        val emailTrim = email.trim()
        val direccionTrim = direccion.trim()
        val rutNormalizado = ValidationUtils.normalizeRut(rut)
        val passTrim = password.trim()

        if (nombreTrim.isEmpty() || emailTrim.isEmpty() || direccionTrim.isEmpty() ||
            rutNormalizado.isEmpty() || passTrim.isEmpty()
        ) {
            mensaje.value = "Todos los campos son obligatorios"
            return false
        }

        // validacion mail
        if (!ValidationUtils.isEmailValid(emailTrim)) {
            mensaje.value = "Email no válido"
            return false
        }

        // validacion rut
        if (!ValidationUtils.isRutValid(rutNormalizado)) {
            mensaje.value = "RUT no válido"
            return false
        }

        val nuevoUsuario = Usuario(
            nombre = nombreTrim,
            email = emailTrim,
            direccion = direccionTrim,
            rut = rutNormalizado,
            password = passTrim,
            rol = "cliente"
        )

        val exito = UserRepository.registrarUsuario(context, nuevoUsuario)
        return if (exito) {
            mensaje.value = "Registro exitoso"
            true
        } else {
            mensaje.value = "El email o RUT ya están registrados"
            false
        }
    }

    fun login(context: Context, email: String, password: String): Boolean {
        val emailTrim = email.trim()
        val passTrim = password.trim()

        if (emailTrim.isEmpty() || passTrim.isEmpty()) {
            mensaje.value = "Por favor ingresa tus credenciales"
            return false
        }

        if (!ValidationUtils.isEmailValid(emailTrim)) {
            mensaje.value = "Email no válido"
            return false
        }

        val usuario = UserRepository.login(context, emailTrim, passTrim)
        return if (usuario != null) {
            usuarioActual.value = usuario
            mensaje.value = "Inicio de sesión exitoso"
            true
        } else {
            mensaje.value = "Credenciales incorrectas"
            false
        }
    }
}
