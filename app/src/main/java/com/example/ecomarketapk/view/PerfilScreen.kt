package com.example.ecomarketapk.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.model.Usuario
import com.example.ecomarketapk.repository.UserRepository
import com.example.ecomarketapk.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val usuarioActual = authViewModel.usuarioActual.value

    // Campos del perfil (pre-cargados si existe usuarioActual)
    var email by remember { mutableStateOf(usuarioActual?.email ?: "") }
    var nombre by remember { mutableStateOf(usuarioActual?.nombre ?: "") }
    var direccion by remember { mutableStateOf(usuarioActual?.direccion ?: "") }
    var nacimiento by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(usuarioActual?.password ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("catalogo") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Imagen de perfil
            Image(
                painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/3177/3177440.png"),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { /* cambiar imagen (futuro) */ }
            )
            Text("Toca la imagen para cambiarla", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            // Campos de perfil
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nacimiento,
                onValueChange = { nacimiento = it },
                label = { Text("Fecha de Nacimiento (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección de Envío") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nueva Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Botón Guardar Cambios
            Button(
                onClick = {
                    if (usuarioActual != null) {
                        val actualizado = Usuario(
                            nombre = nombre,
                            email = email,
                            direccion = direccion,
                            rut = usuarioActual.rut,
                            password = password.ifEmpty { usuarioActual.password },
                            rol = usuarioActual.rol
                        )

                        val exito = UserRepository.actualizarUsuario(context, actualizado)
                        if (exito) {
                            authViewModel.usuarioActual.value = actualizado
                            Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No se pudo actualizar el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Text("Guardar Cambios")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Botón Cerrar Sesión
            TextButton(onClick = {
                authViewModel.usuarioActual.value = null
                navController.navigate("login") {
                    popUpTo("catalogo") { inclusive = true }
                }
            }) {
                Text("Cerrar Sesión")
            }
        }
    }
}
