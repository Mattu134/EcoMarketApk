package com.example.ecomarketapk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.model.Producto
import com.example.ecomarketapk.utils.EcoLogo
import com.example.ecomarketapk.viewmodel.AuthViewModel
import com.example.ecomarketapk.viewmodel.BackOfficeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackOfficeScreen(
    navController: NavController,
    viewModel: BackOfficeViewModel,
    authViewModel: AuthViewModel
) {
    val usuarioActual by authViewModel.usuarioActual
    val inventario by viewModel.inventario.collectAsState()
    val context = LocalContext.current
    val mostrarInventario = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarInventario(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Panel de administración")
                        EcoLogo()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Panel de administración",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (usuarioActual != null) {
                    "Usuario: ${usuarioActual?.nombre} (${usuarioActual?.rol})"
                } else {
                    "Usuario no identificado"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("agregarProducto") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar producto")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { mostrarInventario.value = !mostrarInventario.value },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (mostrarInventario.value) "Ocultar inventario" else "Ver inventario")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al catálogo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mostrarInventario.value) {
                InventarioResumen(inventario = inventario)
                Spacer(modifier = Modifier.height(12.dp))
                InventarioLista(inventario = inventario)
            }
        }
    }
}

@Composable
fun InventarioResumen(inventario: List<Producto>) {
    val totalReferencias = inventario.size
    val totalUnidades = inventario.sumOf { it.stock }
    val valorTotal = inventario.sumOf { it.precio * it.stock }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Resumen de inventario",
            style = MaterialTheme.typography.titleMedium
        )
        Text("Total de referencias: $totalReferencias")
        Text("Total de unidades: $totalUnidades")
        Text(
            "Valor total de inventario: $${String.format(Locale("es", "CL"), "%,.0f", valorTotal)}"
        )
    }
}

@Composable
fun InventarioLista(inventario: List<Producto>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(inventario) { producto ->
            InventarioItem(producto = producto)
        }
    }
}

@Composable
fun InventarioItem(producto: Producto) {
    val valorProducto = producto.precio * producto.stock

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = "Categoría: ${producto.categoria ?: "Sin categoría"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Proveedor: ${producto.proveedor ?: "Sin proveedor"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Lote: ${producto.lote ?: "Sin lote"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Expira: ${producto.fechaExpiracion ?: "Sin fecha"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Stock: ${producto.stock} unidades",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Precio: $${String.format(Locale("es", "CL"), "%,.0f", producto.precio)}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Valor total: $${String.format(Locale("es", "CL"), "%,.0f", valorProducto)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


