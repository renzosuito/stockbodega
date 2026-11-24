package com.example.stockbodega.ui.screens.mas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stockbodega.data.Sesion
import com.example.stockbodega.ui.components.StockTopBar
import com.example.stockbodega.ui.navigation.Rutas

// EXPO: Menú "Más" con el CONTROL DE ROLES.
// Vendedor: solo Kardex (consulta) y cerrar sesión.
// Administrador: además ProvCliente, Reportes, Usuarios y Configuración.
@Composable
fun MasScreen(
    onIr: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = Sesion.usuario
    Scaffold(topBar = { StockTopBar("Más opciones") }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "${usuario?.nombreCompleto ?: ""} · ${usuario?.rol?.lowercase() ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
            Opcion("Kardex de movimientos", "Historial de entradas, salidas y mermas") { onIr(Rutas.KARDEX) }
            if (Sesion.esAdmin) {
                Opcion("Clientes y proveedores", "Directorio ProvCliente") { onIr(Rutas.PROVCLIENTE) }
                Opcion("Reportes", "Ventas, más vendidos y alertas") { onIr(Rutas.REPORTES) }
                Opcion("Usuarios", "Crear usuarios y asignar roles") { onIr(Rutas.USUARIOS) }
                Opcion("Configuración", "Datos de la bodega y copia de seguridad") { onIr(Rutas.CONFIG) }
            }
            HorizontalDivider()
            Opcion("Cerrar sesión", "Salir de la cuenta actual") { onCerrarSesion() }
        }
    }
}

@Composable
private fun Opcion(titulo: String, detalle: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(titulo) },
        supportingContent = { Text(detalle) },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider()
}