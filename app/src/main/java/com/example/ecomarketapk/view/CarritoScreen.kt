package com.example.ecomarketapk.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.utils.EcoLogo
import com.example.ecomarketapk.viewmodel.AuthViewModel
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import com.example.ecomarketapk.viewmodel.MonedaViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun CarritoScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    authViewModel: AuthViewModel,
    monedaViewModel: MonedaViewModel = viewModel()
) {
    val carritoEntries = carritoViewModel.carrito.entries.toList()
    val tasaClpUsd by monedaViewModel.tasaClpUsd.collectAsState()
    val errorTasa by monedaViewModel.error.collectAsState()
    var mostrarUsd by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val usuarioActual = authViewModel.usuarioActual.value
    val esInvitado = usuarioActual?.rol.equals("invitado", ignoreCase = true)
    val carritoVacio = carritoEntries.isEmpty()
    val puedePagar = usuarioActual != null && !esInvitado && !carritoVacio

    LaunchedEffect(Unit) {
        monedaViewModel.cargarTasaClpUsd()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Carrito")
                        Spacer(modifier = Modifier.width(8.dp))
                        EcoLogo()
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (carritoEntries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tu carrito está vacío",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(carritoEntries, key = { it.key.id }) { (producto, cantidad) ->
                            CarritoItemRow(
                                producto = producto,
                                cantidad = cantidad,
                                tasaClpUsd = tasaClpUsd,
                                onIncrementar = { carritoViewModel.agregar(producto) },
                                onDisminuir = { carritoViewModel.disminuir(producto) },
                                onEliminar = { carritoViewModel.eliminarProducto(producto) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Moneda de visualización", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "CLP ${if (mostrarUsd) "+ USD" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = mostrarUsd,
                    onCheckedChange = { mostrarUsd = it },
                    enabled = tasaClpUsd != null
                )
            }

            if (errorTasa != null) {
                Text(
                    text = errorTasa ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val subtotalClp = carritoViewModel.subtotalClp()
            val tasa = tasaClpUsd

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Resumen de compra",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    if (mostrarUsd && tasa != null) {
                        val subtotalUsd = subtotalClp * tasa
                        Text(
                            text = "Subtotal (USD): ${"%.2f".format(subtotalUsd)} USD",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Equivalente CLP: $${String.format(Locale("es", "CL"), "%,.0f", subtotalClp)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    } else {
                        Text(
                            text = "Subtotal (CLP): $${String.format(Locale("es", "CL"), "%,.0f", subtotalClp)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF1B5E20)
                        )
                        if (tasa != null) {
                            val enUsd = subtotalClp * tasa
                            Text(
                                text = "≈ ${"%.2f".format(enUsd)} USD",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        } else {
                            Text(
                                text = "Cargando tasa USD...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (!carritoVacio && !puedePagar) {
                Text(
                    text = "Debes iniciar sesión para poder pagar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    scope.launch {
                        val exito = carritoViewModel.procesarPagoYActualizarStock()
                        if (exito) {
                            navController.navigate("compraExitosa")
                        } else {
                            navController.navigate("compraRechazada")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(999.dp),
                enabled = puedePagar
            ) {
                Text("Pagar")
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun CarritoItemRow(
    producto: ProductoResponse,
    cantidad: Int,
    tasaClpUsd: Double?,
    onIncrementar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    val precioUnit = producto.precioClp.toDouble()
    val totalItem = precioUnit * cantidad
    val tasa = tasaClpUsd

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = "http://3.131.85.198:8080/api/products/${producto.id}/image"
            val painter = rememberAsyncImagePainter(model = imageUrl)

            Image(
                painter = painter,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 2)

                Text(
                    text = "Precio: $${String.format(Locale("es", "CL"), "%,.0f", precioUnit)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Total: $${String.format(Locale("es", "CL"), "%,.0f", totalItem)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                if (tasa != null) {
                    val usd = totalItem * tasa
                    Text(
                        text = "≈ ${"%.2f".format(usd)} USD",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDisminuir) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }

                Text(
                    text = cantidad.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )

                IconButton(onClick = onIncrementar) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
        }
    }
}
