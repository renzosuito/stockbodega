package com.example.stockbodega.ui.screens.productos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockbodega.ui.components.SelectorDesplegable
import com.example.stockbodega.ui.components.StockTopBar
import com.example.stockbodega.util.escanearCodigo
import com.example.stockbodega.viewmodel.ProductoFormViewModel

// EXPO (P04): una sola pantalla sirve para NUEVO (idProducto = 0) y para EDITAR.
// Campos del RF-05: nombre, marca, código, categoría, precio de compra, precio de venta,
// stock mínimo y fecha de vencimiento. El stock actual solo se muestra.
@Composable
fun ProductoFormScreen(
    idProducto: Int,
    onVolver: () -> Unit,
    vm: ProductoFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val categorias by vm.categorias.collectAsState()

    // Si venimos a EDITAR, cargamos los datos del producto una sola vez
    LaunchedEffect(idProducto) {
        if (idProducto != 0) vm.cargar(idProducto)
    }

    Scaffold(
        topBar = { StockTopBar(if (idProducto == 0) "Nuevo producto" else "Editar producto", onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                vm.nombre, { vm.nombre = it },
                label = { Text("Nombre del producto *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                vm.marca, { vm.marca = it },
                label = { Text("Marca *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                vm.codigo, { vm.codigo = it },
                label = { Text("Código / SKU *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    // RF-18: botón para escanear el código de barras con la cámara
                    IconButton(onClick = { escanearCodigo(context) { vm.codigo = it } }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear código")
                    }
                }
            )
            SelectorDesplegable(
                etiqueta = "Categoría *",
                opciones = categorias.map { it.nombre },
                seleccion = categorias.firstOrNull { it.idCategoria == vm.idCategoria }?.nombre
                    ?: "Seleccionar categoría",
                onSeleccion = { vm.idCategoria = categorias[it].idCategoria }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    vm.precioCompra, { vm.precioCompra = it },
                    label = { Text("Precio compra (S/) *") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    vm.precioVenta, { vm.precioVenta = it },
                    label = { Text("Precio venta (S/) *") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                vm.stockMinimo, { vm.stockMinimo = it },
                label = { Text("Stock mínimo *") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                vm.vencimiento, { vm.vencimiento = it },
                label = { Text("Fecha de vencimiento (dd/mm/aaaa)") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Aviso de la regla del negocio
            Text(
                if (vm.esEdicion) "Stock actual: ${vm.stockActual} (solo cambia con guías de ingreso, ventas y mermas)"
                else "El stock empieza en 0 y sube al registrar una guía de internamiento.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            vm.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(onClick = { vm.guardar(onVolver) }, modifier = Modifier.weight(1f)) { Text("Guardar") }
            }

            if (vm.esEdicion) {
                OutlinedButton(onClick = { vm.darDeBaja(onVolver) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Dar de baja el producto")
                }
            }
        }
    }
}