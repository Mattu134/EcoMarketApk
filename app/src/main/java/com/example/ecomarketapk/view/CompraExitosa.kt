package com.example.ecomarketapk.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn // Se sigue usando LazyColumn para los ítems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState // Importar para el scroll
import androidx.compose.foundation.verticalScroll // Importar para el scroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecomarketapk.viewmodel.AuthViewModel
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import com.example.ecomarketapk.viewmodel.MonedaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraExitosaScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    authViewModel: AuthViewModel,
    monedaViewModel: MonedaViewModel
) {
    val items = carritoViewModel.ultimaCompra
    val totalConIva = items.sumOf { (producto, cant) ->
        producto.precioClp.toDouble() * cant
    }
    val subtotal = totalConIva / 1.19
    val iva = totalConIva - subtotal
    val cantidad = items.sumOf { it.second }
    val usuarioActual by authViewModel.usuarioActual
    val direccionEnvio = usuarioActual?.direccion ?: "Sin dirección registrada"
    val tasaClpUsd by monedaViewModel.tasaClpUsd.collectAsState()
    val totalUsd = tasaClpUsd?.let { totalConIva * it }

    val formato = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL"))
    val fechaHora = formato.format(Date())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compra Exitosa") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()), // <--- MODIFICACIÓN CLAVE
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.height(80.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("¡Compra realizada con éxito!", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text(
                "Gracias por comprar en EcoMarket",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Boleta electrónica",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text("Fecha: $fechaHora", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "N° transacción: 0001-0000${(1000..9999).random()}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Dirección de envío:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        direccionEnvio,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(12.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Detalle de productos",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(Modifier.height(8.dp))

                    if (items.isEmpty()) {
                        Text(
                            "No se encontraron productos en esta compra.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp), // <--- SE MANTIENE POR BUENA PRÁCTICA en un Card con scroll
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(items) { (producto, cantidadItem) ->
                                val precioUnit = producto.precioClp.toDouble()
                                val totalLinea = precioUnit * cantidadItem

                                Column(Modifier.fillMaxWidth()) {
                                    Text(
                                        producto.nombre,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        "Cant: $cantidadItem  |  " +
                                                "PU: $${String.format(Locale("es", "CL"), "%,.0f", precioUnit)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "Total ítem: $${String.format(Locale("es", "CL"), "%,.0f", totalLinea)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Divider()
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))

                    // Esta sección ahora será visible gracias al scroll aplicado al Column principal.
                    Text("Items: $cantidad", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Subtotal: $${String.format(Locale("es", "CL"), "%,.0f", subtotal)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "IVA (19%): $${String.format(Locale("es", "CL"), "%,.0f", iva)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Total pagado: $${String.format(Locale("es", "CL"), "%,.0f", totalConIva)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (totalUsd != null) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "≈ ${"%.2f".format(totalUsd)} USD",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

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