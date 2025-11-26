package com.example.ecomarketapk.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ecomarketapk.viewmodel.CatalogoViewModel
import com.example.ecomarketapk.viewmodel.MonedaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    viewModel: CatalogoViewModel,
    carritoViewModel: CarritoViewModel,
    authViewModel: AuthViewModel,
    monedaViewModel: MonedaViewModel = viewModel(),
) {
    val productosFiltrados by viewModel.productosFiltrados.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val carritoCount by remember { derivedStateOf { carritoViewModel.carrito.values.sum() } }
    val esAdmin by remember { derivedStateOf { authViewModel.usuarioActual.value?.rol == "admin" } }
    var showToast by remember { mutableStateOf(false) }
    var toastMsg by remember { mutableStateOf("") }
    val tasaClpUsd by monedaViewModel.tasaClpUsd.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
        monedaViewModel.cargarTasaClpUsd()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "EcoMarket")
                        EcoLogo()
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                        if (carritoCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Box(Modifier.size(18.dp))
                                }
                                Text(
                                    text = carritoCount.toString(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
                if (esAdmin) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("backoffice") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "BackOffice") },
                        label = { Text("BackOffice") }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    label = { Text("Buscar producto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryButton(
                            "Todos",
                            categoriaSeleccionada == null
                        ) { viewModel.onCategoriaSeleccionada(null) }
                    }
                    items(categorias.size) { i ->
                        val cat = categorias[i]
                        CategoryButton(cat, categoriaSeleccionada == cat) {
                            viewModel.onCategoriaSeleccionada(cat)
                        }
                    }
                }

                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(productosFiltrados, key = { it.id }) { producto ->
                            val imageUrl =
                                "http://3.17.39.248:8080/api/products/${producto.id}/image"

                            ProductoCardGrid(
                                producto = producto,
                                imageUrl = imageUrl,
                                tasaClpUsd = tasaClpUsd,
                                onAgregar = {
                                    carritoViewModel.agregar(producto)
                                    toastMsg = "${producto.nombre} agregado con éxito"
                                    showToast = true
                                },
                                onVerDetalle = {
                                    navController.navigate("detalle/${producto.id}")
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun CategoryButton(nombre: String, seleccionado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White
        )
    ) { Text(nombre) }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ProductoCardGrid(
    producto: ProductoResponse,
    imageUrl: String,
    tasaClpUsd: Double?,
    onAgregar: () -> Unit,
    onVerDetalle: () -> Unit,
) {
    val precioClp = producto.precioClp.toDouble()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerDetalle() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            val painter = rememberAsyncImagePainter(model = imageUrl)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) //
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(8.dp))
            producto.categoria?.let { categoria ->
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x332E7D32))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2
            )

            Spacer(Modifier.height(2.dp))
            Text(
                text = "$${String.format(Locale("es", "CL"), "%,.0f", precioClp)}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF388E3C)
            )
            if (tasaClpUsd != null) {
                val precioUsd = precioClp * tasaClpUsd
                Text(
                    text = "≈ ${"%.2f".format(precioUsd)} USD",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "Cargando USD...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onAgregar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Agregar")
            }
        }
    }
}

