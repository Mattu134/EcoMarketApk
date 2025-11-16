package com.example.ecomarketapk.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecomarketapk.viewmodel.BackOfficeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(navController: NavController, viewModel: BackOfficeViewModel) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagen by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Selecciona una categoría") }

    var stock by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }

    var mensajeExito by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val categorias = listOf("Frutas", "Verduras", "Bebidas", "Lácteos", "Snacks", "Aseo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                        Text("Agregar Producto")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precio = it },
                label = { Text("Precio") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del producto") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = imagen,
                onValueChange = { imagen = it },
                label = { Text("URL o nombre de la imagen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = stock,
                onValueChange = { if (it.all { c -> c.isDigit() }) stock = it },
                label = { Text("Stock (unidades)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proveedor,
                onValueChange = { proveedor = it },
                label = { Text("Proveedor") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lote,
                onValueChange = { lote = it },
                label = { Text("Lote") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaExpiracion,
                onValueChange = { fechaExpiracion = it },
                label = { Text("Fecha de expiración (ej: 2025-11-05)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val resultado = viewModel.agregarProducto(
                        context = context,
                        nombre = nombre,
                        precio = precio,
                        descripcion = descripcion,
                        imagen = imagen,
                        categoria = if (categoria == "Selecciona una categoría") "" else categoria,
                        stock = stock,
                        proveedor = proveedor,
                        lote = lote,
                        fechaExpiracion = fechaExpiracion
                    )
                    Toast.makeText(context, resultado.mensaje, Toast.LENGTH_SHORT).show()
                    if (resultado.exito) {
                        mensajeExito = true
                        nombre = ""
                        precio = ""
                        descripcion = ""
                        imagen = ""
                        categoria = "Selecciona una categoría"
                        stock = ""
                        proveedor = ""
                        lote = ""
                        fechaExpiracion = ""
                        viewModel.cargarInventario(context)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Producto")
            }

            if (mensajeExito) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    mensajeExito = false
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Producto agregado correctamente", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
