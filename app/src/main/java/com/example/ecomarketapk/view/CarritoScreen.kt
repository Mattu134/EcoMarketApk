package com.example.ecomarketapk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecomarketapk.R
import com.example.ecomarketapk.model.Producto
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, carritoViewModel: CarritoViewModel) {
    val carrito = carritoViewModel.carrito
    val subtotal = carritoViewModel.subtotal()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("catalogo") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Carrito", style = MaterialTheme.typography.titleMedium)
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo EcoMarket",
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .height(32.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Subtotal: $${String.format(Locale("es", "CL"), "%,.0f", subtotal)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val exito = carritoViewModel.pagar()
                        if (exito) {
                            navController.navigate("compraExitosa")
                        } else {
                            navController.navigate("compraRechazada")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Pagar")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .padding(8.dp)
        ) {
            items(carrito.keys.toList()) { producto ->
                val cantidad = carrito[producto] ?: 0
                CarritoItem(
                    producto = producto,
                    cantidad = cantidad,
                    onIncrementar = { carritoViewModel.agregar(producto) },
                    onDecrementar = { carritoViewModel.disminuir(producto) },
                    onEliminar = { carritoViewModel.eliminarProducto(producto) }
                )
            }
        }
    }
}

@Composable
fun CarritoItem(
    producto: Producto,
    cantidad: Int,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text("Precio unitario: $${String.format(Locale("es", "CL"), "%,.0f", producto.precio)}")
            Text("Total: $${String.format(Locale("es", "CL"), "%,.0f", producto.precio * cantidad)}")

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrementar) { Icon(Icons.Default.Remove, null) }
                    Text("$cantidad")
                    IconButton(onClick = onIncrementar) { Icon(Icons.Default.Add, null) }
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
