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
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraExitosaScreen(navController: NavController, carritoViewModel: CarritoViewModel) {
    val subtotal = carritoViewModel.subtotal()
    val cantidad = carritoViewModel.carrito.values.sum()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compra Exitosa") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("¡Compra realizada con éxito!", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                "Has adquirido $cantidad producto(s) por un total de $${String.format(Locale("es", "CL"), "%,.0f", subtotal)}"
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    carritoViewModel.limpiar()
                    navController.navigate("catalogo") {
                        popUpTo("catalogo") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seguir comprando")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    carritoViewModel.limpiar()
                    navController.navigate("catalogo") {
                        popUpTo("catalogo") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}
