package com.example.ecomarketapk.repository

import android.content.Context
import com.example.ecomarketapk.model.Usuario
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object UserRepository {

    private const val JSON_FILE_NAME = "usuarios.json"
    private val gson = Gson()

    private fun getRuntimeFile(context: Context): File {
        return File(context.filesDir, JSON_FILE_NAME)
    }

    private fun loadUsers(context: Context): MutableList<Usuario> {
        val file = getRuntimeFile(context)

        if (!file.exists()) {
            try {
                context.assets.open(JSON_FILE_NAME).use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
            } catch (_: Exception) {
                file.writeText("[]")
            }
        }

        return try {
            FileReader(file).use { reader ->
                val type = object : TypeToken<MutableList<Usuario>>() {}.type
                gson.fromJson(reader, type) ?: mutableListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    private fun saveUsers(context: Context, users: List<Usuario>) {
        try {
            FileWriter(getRuntimeFile(context)).use { writer ->
                gson.toJson(users, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun registrarUsuario(context: Context, nuevoUsuario: Usuario): Boolean {
        val usuarios = loadUsers(context)
        if (usuarios.any { it.email == nuevoUsuario.email || it.rut == nuevoUsuario.rut }) {
            return false
        }
        usuarios.add(nuevoUsuario)
        saveUsers(context, usuarios)
        return true
    }

    fun login(context: Context, email: String, password: String): Usuario? {
        val usuarios = loadUsers(context)
        return usuarios.find { it.email == email && it.password == password }
    }

    fun obtenerUsuarios(context: Context): List<Usuario> = loadUsers(context)

    fun limpiarUsuarios(context: Context) = saveUsers(context, emptyList())

    fun actualizarUsuario(context: Context, usuarioActualizado: Usuario): Boolean {
        val usuarios = loadUsers(context)
        val index = usuarios.indexOfFirst { it.email == usuarioActualizado.email }

        return if (index != -1) {
            usuarios[index] = usuarioActualizado
            saveUsers(context, usuarios)
            true
        } else {
            false
        }
    }
}
