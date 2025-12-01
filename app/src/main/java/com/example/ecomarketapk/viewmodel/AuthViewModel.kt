package com.example.ecomarketapk.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.ecomarketapk.model.Usuario
import com.example.ecomarketapk.repository.UserRepository
import com.example.ecomarketapk.utils.ValidationUtils

class AuthViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    val usuarioActual = mutableStateOf<Usuario?>(null)
    val esInvitado = mutableStateOf(false)
    val mensaje = mutableStateOf("")

    fun login(context: Context, email: String, password: String): Boolean {
        esInvitado.value = false

        if (email.isBlank() || password.isBlank()) {
            mensaje.value = "Completa todos los campos"
            return false
        }
        if (!ValidationUtils.isEmailValid(email)) {
            mensaje.value = "Email inválido"
            return false
        }

        val usuario = userRepository.login(context, email, password)
        return if (usuario != null) {
            usuarioActual.value = usuario
            mensaje.value = ""
            true
        } else {
            mensaje.value = "Email o contraseña incorrectos"
            false
        }
    }

    fun loginInvitado() {
        usuarioActual.value = Usuario(
            nombre = "Invitado",
            email = "",
            direccion = "",
            rut = "",
            password = "",
            rol = "invitado"
        )
        esInvitado.value = true
        mensaje.value = ""
    }

    fun registrar(
        context: Context,
        nombre: String,
        email: String,
        direccion: String,
        rut: String,
        password: String
    ): Boolean {
        esInvitado.value = false

        if (nombre.isBlank() || email.isBlank() || direccion.isBlank() || rut.isBlank() || password.isBlank()) {
            mensaje.value = "Completa todos los campos"
            return false
        }
        if (!ValidationUtils.isEmailValid(email)) {
            mensaje.value = "Email inválido"
            return false
        }
        if (!ValidationUtils.isRutValid(rut)) {
            mensaje.value = "RUT inválido"
            return false
        }

        val usuario = Usuario(
            nombre = nombre,
            email = email,
            direccion = direccion,
            rut = rut,
            password = password,
            rol = "cliente"
        )
        val exito = userRepository.registrarUsuario(context, usuario)
        mensaje.value = if (exito) "Usuario registrado correctamente" else "El usuario ya existe"

        if (exito) {
            usuarioActual.value = usuario
        }
        return exito
    }

    fun actualizarUsuario(context: Context, usuario: Usuario): Boolean {
        val exito = userRepository.actualizarUsuario(context, usuario)
        if (exito) {
            usuarioActual.value = usuario
        }
        return exito
    }

    fun logout() {
        usuarioActual.value = null
        esInvitado.value = false
        mensaje.value = ""
    }

    fun clearMensaje() {
        mensaje.value = ""
    }

    fun puedeComprar(): Boolean {
        val user = usuarioActual.value
        if (user == null) return false
        if (esInvitado.value) return false
        if (user.rol == "invitado") return false
        return true
    }
}
