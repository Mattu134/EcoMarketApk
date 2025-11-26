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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.utils.EcoLogo
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
    monedaViewModel: MonedaViewModel = viewModel()
) { val carritoEntries = carritoViewModel.carrito.entries.toList()
    val tasaClpUsd by monedaViewModel.tasaClpUsd.collectAsState()
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Carrito")
                        Spacer(modifier = Modifier.size(8.dp))
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
                shape = RoundedCornerShape(12.dp)
            ) {
                if (carritoEntries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tu carrito está vacío")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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

            // SUBTOTAL
            val subtotal = carritoViewModel.subtotalClp()
            Text(
                text = "Subtotal: $${String.format(Locale("es", "CL"), "%,.0f", subtotal)}",
                style = MaterialTheme.typography.bodyLarge
            )

            // mostrar total en USD
            if (tasaClpUsd != null) {
                val totalUsd = subtotal * tasaClpUsd!!
                Text(
                    text = "≈ ${"%.2f".format(totalUsd)} USD",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


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
                modifier = Modifier.fillMaxWidth()
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl =
                "http://3.17.39.248:8080/api/products/${producto.id}/image"
            val painter =
                rememberAsyncImagePainter(model = imageUrl)

            Image(
                painter = painter,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                Text(
                    text = "Precio: $${String.format(Locale("es", "CL"), "%,.0f", precioUnit)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Total: $${String.format(Locale("es", "CL"), "%,.0f", totalItem)}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (tasaClpUsd != null) {
                    val usd = totalItem * tasaClpUsd
                    Text(
                        text = "≈ ${"%.2f".format(usd)} USD",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onDisminuir()
                    }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }

                Text(
                    text = cantidad.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )

                IconButton(
                    onClick = {
                        onIncrementar()
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }
        }
    }
}
