package com.example.ecomarketapk.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import com.example.ecomarketapk.viewmodel.CatalogoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Int,
    viewModel: CatalogoViewModel,
    carritoViewModel: CarritoViewModel,
    navController: NavController
) {
    val producto = remember { viewModel.buscarProductoPorId(productoId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                        Text("Volver", style = MaterialTheme.typography.bodyMedium)
                    }
                },
                actions = {
                    Text(
                        producto?.nombre ?: "Detalle",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        producto?.let { p ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // ✅ Scroll completo
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Imagen principal
                Image(
                    painter = rememberAsyncImagePainter(p.imagen),
                    contentDescription = p.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Imágenes adicionales simuladas
                val imagenesAdicionales = listOf(
                    p.imagen,
                    "https://images.unsplash.com/photo-1565958011705-44e2119023b2?auto=format&fit=crop&w=400&q=60",
                    "https://images.unsplash.com/photo-1574226516831-e1dff420e12e?auto=format&fit=crop&w=400&q=60"
                )

                Text(
                    "Más imágenes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(imagenesAdicionales) { img ->
                        Image(
                            painter = rememberAsyncImagePainter(img),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Descripción extendida
                Text(
                    text = p.descripcion ?: "Sin descripción disponible.",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Precio
                Text(
                    text = "Precio: $${String.format(Locale("es", "CL"), "%,.0f", p.precio)}",
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                )

                // Botón para agregar al carrito
                Button(
                    onClick = {
                        carritoViewModel.agregar(p)
                        navController.navigate("carrito")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Agregar al carrito")
                }

                // Opiniones
                Text(
                    "Opiniones de usuarios",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OpinionesList() // ✅ Versión con estrellas variables y scroll habilitado
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Producto no encontrado")
        }
    }
}

@Composable
fun OpinionesList() {
    val opiniones = listOf(
        Triple("Excelente producto, llegó a tiempo y en buen estado.", 5, "Juan"),
        Triple("Buena calidad, pero el empaque podría mejorar.", 4, "María"),
        Triple("El precio está bien, pero me gustaría más variedad.", 3, "Pedro"),
        Triple("Muy recomendado, cumple lo que promete.", 5, "Sofía"),
        Triple("No me gustó, esperaba más calidad.", 2, "Luis"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        opiniones.forEach { (comentario, estrellas, usuario) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(usuario, fontWeight = FontWeight.Bold)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Estrella",
                                tint = if (index < estrellas) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(comentario, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
