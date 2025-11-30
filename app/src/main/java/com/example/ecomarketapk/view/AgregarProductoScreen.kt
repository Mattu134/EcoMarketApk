package com.example.ecomarketapk.view

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ecomarketapk.viewmodel.BackOfficeViewModel
import com.example.ecomarketapk.viewmodel.CatalogoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    navController: NavController,
    viewModel: BackOfficeViewModel,
    catalogoViewModel: CatalogoViewModel
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    // Usar KeyboardType.Decimal para permitir un separador decimal si es necesario
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var nombreImagen by remember { mutableStateOf("Ninguna imagen seleccionada") }
    var categoria by remember { mutableStateOf("Selecciona una categoría") }
    var usandoCategoriaNueva by remember { mutableStateOf(false) }
    var nuevaCategoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var lote by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val categoriasBase = listOf("Frutas", "Verduras", "Bebidas", "Lácteos", "Snacks", "Aseo")
    val categorias = categoriasBase + listOf("Agregar nueva categoría...")

    val launcherImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        imagenUri = uriSeleccionado
        nombreImagen = if (uriSeleccionado != null) "Imagen seleccionada" else "Ninguna imagen seleccionada"
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            val hoy = Calendar.getInstance()
            val anio = hoy.get(Calendar.YEAR)
            val mes = hoy.get(Calendar.MONTH)
            val dia = hoy.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val calSeleccionado = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fechaExpiracion = formato.format(calSeleccionado.time)
                },
                anio,
                mes,
                dia
            )
            datePicker.datePicker.minDate = hoy.timeInMillis
            datePicker.show()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Column {
                        Text("Agregar Producto")
                        Text(
                            text = "Completa los datos para registrar un nuevo producto",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Información del producto",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del producto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Precio (CLP)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción del producto") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Imagen del producto",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenUri != null) {
                            val painter = rememberAsyncImagePainter(model = imagenUri)
                            Image(
                                painter = painter,
                                contentDescription = "Vista previa del producto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.height(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Sin imagen seleccionada",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { launcherImagen.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Seleccionar imagen desde galería")
                    }

                    Text(
                        text = nombreImagen,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Categoría y stock",
                        style = MaterialTheme.typography.titleMedium
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = if (usandoCategoriaNueva && nuevaCategoria.isNotBlank())
                                nuevaCategoria
                            else
                                categoria,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
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
                                        when (opcion) {
                                            "Agregar nueva categoría..." -> {
                                                usandoCategoriaNueva = true
                                                categoria = "Nueva categoría"
                                            }

                                            else -> {
                                                usandoCategoriaNueva = false
                                                categoria = opcion
                                                nuevaCategoria = ""
                                            }
                                        }
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (usandoCategoriaNueva) {
                        OutlinedTextField(
                            value = nuevaCategoria,
                            onValueChange = { nuevaCategoria = it },
                            label = { Text("Nombre de la nueva categoría") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { if (it.all { c -> c.isDigit() }) stock = it },
                        label = { Text("Stock (unidades)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Información adicional",
                        style = MaterialTheme.typography.titleMedium
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
                        onValueChange = {  },
                        label = { Text("Fecha de expiración") },
                        singleLine = true,
                        readOnly = true,
                        interactionSource = interactionSource,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Seleccionar fecha"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val categoriaFinal = when {
                            usandoCategoriaNueva && nuevaCategoria.isNotBlank() ->
                                nuevaCategoria.trim()

                            categoria == "Selecciona una categoría" ->
                                ""

                            else ->
                                categoria
                        }

                        val resultado = viewModel.agregarProducto(
                            context = context,
                            nombre = nombre,
                            precio = precio,
                            descripcion = descripcion,
                            imagenUri = imagenUri,
                            categoria = categoriaFinal,
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
                            categoria = "Selecciona una categoría"
                            usandoCategoriaNueva = false
                            nuevaCategoria = ""
                            stock = ""
                            proveedor = ""
                            lote = ""
                            fechaExpiracion = ""
                            imagenUri = null
                            nombreImagen = "Ninguna imagen seleccionada"
                            // Recargar productos
                            catalogoViewModel.cargarProductos()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = nombre.isNotBlank() && precio.isNotBlank() && stock.isNotBlank() && imagenUri != null && categoria != "Selecciona una categoría"
            ) {
                Text("Guardar Producto")
            }
            if (mensajeExito) {
                LaunchedEffect(mensajeExito) {
                    delay(2000)
                    mensajeExito = false
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Éxito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Producto agregado correctamente",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}