package com.example.ecomarketapk.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecomarketapk.viewmodel.BackOfficeViewModel
import com.example.ecomarketapk.viewmodel.ResultadoOperacion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    navController: NavController,
    productoId: Long,
    viewModel: BackOfficeViewModel
) {
    val productoEnEdicion by viewModel.productoEnEdicion.collectAsState()
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf<String?>(null) }

    var showSuccessToast by remember { mutableStateOf(false) }
    var toastMsg by remember { mutableStateOf("") }

    LaunchedEffect(productoId) {
        viewModel.cargarProductoParaEdicion(productoId)
    }
    LaunchedEffect(productoEnEdicion) {
        productoEnEdicion?.let { p ->
            nombre = p.nombre ?: ""
            descripcion = p.descripcion ?: ""
            precio = p.precioClp.toString()
            stock = p.stock.toString()
            categoria = p.categoria ?: ""
            proveedor = p.proveedor ?: ""
            lote = p.numeroLote ?: ""
            fechaExpiracion = p.fechaExpiracion ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar producto") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (productoEnEdicion == null) {
                    Text("Cargando producto...", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = precio,
                                onValueChange = { precio = it },
                                label = { Text("Precio CLP") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = stock,
                                onValueChange = { stock = it },
                                label = { Text("Stock") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = categoria,
                                onValueChange = { categoria = it },
                                label = { Text("Categoría") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = proveedor,
                                onValueChange = { proveedor = it },
                                label = { Text("Proveedor") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = lote,
                                onValueChange = { lote = it },
                                label = { Text("Número de lote") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = fechaExpiracion,
                                onValueChange = { fechaExpiracion = it },
                                label = { Text("Fecha expiración (YYYY-MM-DD)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (mensajeError != null) {
                                Text(
                                    text = mensajeError!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Cancelar")
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            val resultado: ResultadoOperacion =
                                                viewModel.actualizarProducto(
                                                    id = productoId,
                                                    nombre = nombre,
                                                    precio = precio,
                                                    descripcion = descripcion,
                                                    categoria = categoria,
                                                    stock = stock,
                                                    proveedor = proveedor,
                                                    lote = lote,
                                                    fechaExpiracion = fechaExpiracion
                                                )

                                            if (resultado.exito) {
                                                mensajeError = null
                                                toastMsg = "Cambios realizados con éxito"
                                                showSuccessToast = true
                                                delay(1200)
                                                navController.popBackStack()
                                            } else {
                                                mensajeError = resultado.mensaje
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Guardar cambios")
                                }
                            }
                        }
                    }
                }
            }
            if (showSuccessToast) {
                CenterToast(
                    message = toastMsg,
                    onDismiss = { showSuccessToast = false },
                    durationMillis = 1200
                )
            }
        }
    }
}


@Composable
fun CenterToast(
    message: String,
    onDismiss: () -> Unit,
    durationMillis: Long = 1500
) {
    LaunchedEffect(Unit) {
        delay(durationMillis)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(
                Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
