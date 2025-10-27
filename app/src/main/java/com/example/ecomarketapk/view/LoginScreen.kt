package com.example.ecomarketapk.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecomarketapk.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    // 🔹 Borra automáticamente el mensaje después de 3 segundos
    val mensaje = viewModel.mensaje.value
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            delay(3000)
            viewModel.mensaje.value = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "EcoMarket",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text("Inicio de Sesión", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (viewModel.login(context, email, password)) {
                val usuario = viewModel.usuarioActual.value
                if (usuario?.rol == "admin") {
                    navController.navigate("backoffice") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("catalogo") {
                        popUpTo("login") { inclusive = true }
                    }
                }
                viewModel.mensaje.value = "" // limpiar mensaje tras navegar
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar")
        }

        // 🔹 Botón para ir al registro
        TextButton(
            onClick = {
                viewModel.mensaje.value = "" // limpiar mensajes previos
                navController.navigate("register") {
                    popUpTo("login") { inclusive = false }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("¿No tienes cuenta? Crear cuenta")
        }

        // 🔹 Mensaje temporal (éxito o error)
        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(top = 10.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
