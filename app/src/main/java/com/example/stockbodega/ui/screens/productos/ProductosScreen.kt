package com.example.stockbodega.ui.screens.productos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockbodega.data.Sesion
import com.example.stockbodega.data.model.Producto
import com.example.stockbodega.ui.components.EstadoVacio
import com.example.stockbodega.ui.components.FiltroChips
import com.example.stockbodega.ui.components.StockTopBar
import com.example.stockbodega.util.fechaCorta
import com.example.stockbodega.util.soles
import com.example.stockbodega.viewmodel.FiltroStock
import com.example.stockbodega.viewmodel.ProductosViewModel

// EXPO (RF-06 / P03): catálogo con buscador y filtros. El VENDEDOR solo consulta;
// el ADMINISTRADOR además ve el botón "Nuevo" y puede tocar un producto para editarlo.
@Composable
fun ProductosScreen(
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit,
    vm: ProductosViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val texto by vm.texto.collectAsState()
    val esAdmin = Sesion.esAdmin

    Scaffold(
        topBar = { StockTopBar("Productos") },
        floatingActionButton = {
            if (esAdmin) {
                ExtendedFloatingActionButton(
                    onClick = onNuevo,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nuevo") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = texto,
                onValueChange = vm::cambiarTexto,
                label = { Text("Buscar por nombre, marca o código") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            FiltroChips(
                opciones = FiltroStock.entries.toList(),
                seleccionada = filtro,
                etiqueta = { it.etiqueta },
                onSeleccion = vm::cambiarFiltro
            )
            Spacer(Modifier.height(8.dp))
            if (productos.isEmpty()) {
                EstadoVacio("No hay productos para mostrar")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(productos, key = { it.idProducto }) { p ->
                        val categoria = categorias.firstOrNull { it.idCategoria == p.idCategoria }?.nombre ?: ""
                        TarjetaProducto(p, categoria, editable = esAdmin) { onEditar(p.idProducto) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaProducto(
    p: Producto,
    categoria: String,
    editable: Boolean,
    onClick: () -> Unit
) {
    val bajo = p.stockActual <= p.stockMinimo
    Card(onClick = onClick, enabled = editable, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(p.nombre, fontWeight = FontWeight.Bold)
                Text("${p.marca} · $categoria", style = MaterialTheme.typography.bodySmall)
                Text("Código: ${p.codigoBarras}", style = MaterialTheme.typography.bodySmall)
                p.fechaVencimiento?.let {
                    Text("Vence: ${it.fechaCorta()}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(p.precioVenta.soles(), fontWeight = FontWeight.Bold)
                // Rojo cuando el stock llegó al mínimo: así se nota la alerta (RF-11)
                Text(
                    if (p.stockActual == 0) "Sin stock" else "Stock: ${p.stockActual}",
                    color = if (bajo) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}