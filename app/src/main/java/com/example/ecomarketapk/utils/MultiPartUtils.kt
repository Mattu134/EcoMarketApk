package com.example.ecomarketapk.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun crearImagenPartDesdeUri(
    context: Context,
    uri: Uri,
    nombreCampo: String = "imagen",
    nombreArchivo: String = "producto.jpg"
): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("No se pudo abrir la imagen seleccionada")

    // Lee todos los bytes de la imagen
    val bytes = inputStream.readBytes()
    inputStream.close()

    val requestFile: RequestBody = bytes.toRequestBody(
        "image/*".toMediaType()
    )

    return MultipartBody.Part.createFormData(
        nombreCampo,
        nombreArchivo,
        requestFile
    )
}
