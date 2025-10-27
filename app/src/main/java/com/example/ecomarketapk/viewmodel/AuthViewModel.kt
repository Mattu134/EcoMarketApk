package com.example.ecomarketapk.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.ecomarketapk.model.Usuario
import com.example.ecomarketapk.repository.UserRepository
import com.example.ecomarketapk.utils.ValidationUtils

class AuthViewModel : ViewModel() {

    // Mensaje observable (se puede mostrar en el UI)
    var mensaje = mutableStateOf("")

    // Usuario actual (para usarlo en HomeScreen, etc.)
    var usuarioActual = mutableStateOf<Usuario?>(null)

    // 🔹 Registrar nuevo usuario
    fun registrar(
        context: Context,
        nombre: String,
        email: String,
        direccion: String,
        rut: String,
        password: String
    ): Boolean {
        // Validaciones básicas
        if (nombre.isEmpty() || email.isEmpty() || direccion.isEmpty() || rut.isEmpty() || password.isEmpty()) {
            mensaje.value = "Todos los campos son obligatorios"
            return false
        }

        if (!ValidationUtils.isEmailValid(email)) {
            mensaje.value = "Email no válido"
            return false
        }

        // Crear el objeto Usuario (debes asegurarte que coincida con tu modelo)
        val nuevoUsuario = Usuario(
            nombre = nombre,
            email = email,
            direccion = direccion,
            rut = rut,
            password = password,
            rol = "cliente"
        )

        // Llamar al repositorio
        val exito = UserRepository.registrarUsuario(context, nuevoUsuario)
        if (exito) {
            mensaje.value = "✅ Registro exitoso"
            return true
        } else {
            mensaje.value = "⚠️ El email o RUT ya están registrados"
            return false
        }
    }

    // 🔹 Iniciar sesión
    fun login(context: Context, email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            mensaje.value = "Por favor ingresa tus credenciales"
            return false
        }

        if (!ValidationUtils.isEmailValid(email)) {
            mensaje.value = "Email no válido"
            return false
        }

        val usuario = UserRepository.login(context, email, password)
        return if (usuario != null) {
            usuarioActual.value = usuario
            mensaje.value = "Inicio de sesión exitoso ✅"
            true
        } else {
            mensaje.value= "Credenciales incorrectas ⚠️"
            false
        }
    }
}


