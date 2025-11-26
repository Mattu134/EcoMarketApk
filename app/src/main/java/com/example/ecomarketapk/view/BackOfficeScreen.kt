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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
    val mostrarInventario = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.cargarInventario()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Panel de administración",
                            style = MaterialTheme.typography.titleMedium
                        )
                        EcoLogo()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Bienvenido al BackOffice",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (usuarioActual != null) {
                    "Usuario: ${usuarioActual?.nombre} (${usuarioActual?.rol})"
                } else {
                    "Usuario no identificado"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate("agregarProducto") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Agregar producto")
                }

                Button(
                    onClick = { navController.navigate("catalogo") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Ver catálogo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { mostrarInventario.value = !mostrarInventario.value },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    if (mostrarInventario.value) "Ocultar inventario" else "Ver inventario",
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            if (mostrarInventario.value) {
                InventarioResumen(inventario = inventario)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Detalle de inventario",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                InventarioLista(
                    inventario = inventario,
                    navController = navController
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Inventario oculto",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Pulsa en \"Ver inventario\" para revisar los productos.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InventarioResumen(inventario: List<Producto>) {
    val totalReferencias = inventario.size
    val totalUnidades = inventario.sumOf { it.stock }
    val valorTotal = inventario.sumOf { it.precioClp * it.stock }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Resumen de inventario",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Total de referencias: $totalReferencias",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                "Total de unidades: $totalUnidades",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                "Valor total de inventario: $${String.format(Locale("es", "CL"), "%,d", valorTotal)}",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun InventarioLista(
    inventario: List<Producto>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(inventario) { producto ->
            InventarioItem(
                producto = producto,
                onEditar = {
                    navController.navigate("editarProducto/${producto.id}")
                }
            )
        }
    }
}

@Composable
fun InventarioItem(
    producto: Producto,
    onEditar: () -> Unit
) {
    val valorProducto = producto.precioClp * producto.stock
    val imageUrl = "http://3.17.39.248:8080/api/products/${producto.id}/image"
    val painter = rememberAsyncImagePainter(model = imageUrl)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painter,
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .height(80.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        producto.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Categoría: ${producto.categoria ?: "Sin categoría"}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Proveedor: ${producto.proveedor ?: "Sin proveedor"}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Lote: ${producto.numeroLote ?: "Sin lote"}",
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
                        text = "Precio: $${String.format(Locale("es", "CL"), "%,d", producto.precioClp)}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Valor total: $${String.format(Locale("es", "CL"), "%,d", valorProducto)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onEditar,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Editar")
                }
            }
        }
    }
}
