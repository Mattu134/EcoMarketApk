package com.example.ecomarketapk.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraRechazadaScreen(navController: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Compra Rechazada") }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFF44336),
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("¡Ups! Ocurrió un problema con tu compra.", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Tu pago fue rechazado o el producto no tiene stock suficiente.")
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("carrito") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al carrito")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seguir comprando")
            }
        }
    }
}
