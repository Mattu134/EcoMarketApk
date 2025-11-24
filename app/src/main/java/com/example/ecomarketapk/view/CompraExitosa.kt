package com.example.ecomarketapk.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraExitosaScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel
) {
    val items = carritoViewModel.ultimaCompra

    val cantidad = items.sumOf { it.second }
    val subtotal = items.sumOf { (producto, cant) ->
        producto.precioClp.toDouble() * cant
    }

    val formato = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL"))
    val fechaHora = formato.format(Date())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Compra Exitosa") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
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

            // 🧾 Boleta
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
                                .height(200.dp),
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

                    Text(
                        "Items: $cantidad",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Total pagado: $${String.format(Locale("es", "CL"), "%,.0f", subtotal)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
