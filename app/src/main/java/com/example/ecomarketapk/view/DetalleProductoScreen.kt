package com.example.ecomarketapk.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.data.ProductoResponse
import com.example.ecomarketapk.utils.SaludUtils
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
    val productos by viewModel.productos.collectAsState()
    val producto: ProductoResponse? = productos.firstOrNull { it.id.toInt() == productoId }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Text(
                        text = producto?.nombre ?: "Detalle del producto",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }
            )
        }
    ) { padding ->

        producto?.let { p ->
            val imageUrl = "http://3.131.85.198:8080/api/products/${producto.id}/image"

            val ratingSalud = SaludUtils.calcularSaludRating(p)
            val textoSalud = SaludUtils.textoNivelSalud(ratingSalud)

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = p.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!p.categoria.isNullOrBlank()) {
                        SimplePill(
                            icon = Icons.Default.LocalOffer,
                            text = p.categoria!!
                        )
                    }
                    SimplePill(
                        icon = Icons.Default.Info,
                        text = "Stock: ${p.stock}"
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5FFF7)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = p.nombre,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            for (i in 1..5) {
                                val filled = i <= ratingSalud
                                Icon(
                                    imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = when (ratingSalud) {
                                        5, 4 -> Color(0xFF2E7D32)
                                        3 -> Color(0xFFF9A825)
                                        else -> Color(0xFFC62828)
                                    },
                                    modifier = Modifier.height(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = textoSalud,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = p.descripcion ?: "Sin descripción disponible.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Precio",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray
                        )

                        Text(
                            text = "$${String.format(Locale("es", "CL"), "%,.0f", p.precioClp.toDouble())} CLP",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            if (!p.proveedor.isNullOrBlank()) {
                                Text(
                                    text = "Proveedor: ${p.proveedor}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (!p.numeroLote.isNullOrBlank()) {
                                Text(
                                    text = "Lote: ${p.numeroLote}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (!p.fechaExpiracion.isNullOrBlank()) {
                                Text(
                                    text = "Fecha de expiración: ${p.fechaExpiracion}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        carritoViewModel.agregar(p)
                        navController.navigate("carrito")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Agregar al carrito")
                }

                Text(
                    "Opiniones de usuarios",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OpinionesList()
            }
        } ?: Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Producto no encontrado")
        }
    }
}

@Composable
private fun SimplePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.height(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun OpinionesList() {
    val opiniones = listOf(
        Triple("Excelente producto, llegó a tiempo y en buen estado.", 5, "Juan"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        opiniones.forEach { (comentario, estrellas, usuario) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                modifier = Modifier.height(18.dp)
                            )
                        }
                    }
                    Text(comentario, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
