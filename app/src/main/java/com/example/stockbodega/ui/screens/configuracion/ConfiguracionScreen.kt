package com.example.stockbodega.ui.screens.configuracion

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockbodega.ui.components.StockTopBar
import com.example.stockbodega.viewmodel.ConfiguracionViewModel

// EXPO (RF-03 y RF-17): datos del negocio (salen en el ticket) y copia de seguridad local.
// Solo el ADMINISTRADOR entra aquí.
@Composable
fun ConfiguracionScreen(
    onVolver: () -> Unit,
    vm: ConfiguracionViewModel = viewModel()
) {
    val context = LocalContext.current
    var confirmarRestaurar by remember { mutableStateOf(false) }

    Scaffold(topBar = { StockTopBar("Configuración", onVolver) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Datos de la bodega", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                vm.nombreComercial, { vm.nombreComercial = it },
                label = { Text("Nombre comercial *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                vm.ruc, { vm.ruc = it },
                label = { Text("RUC (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                vm.direccion, { vm.direccion = it },
                label = { Text("Dirección") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                vm.telefono, { vm.telefono = it },
                label = { Text("Teléfono") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { vm.guardar() }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar cambios")
            }

            HorizontalDivider()

            Text("Copia de seguridad", style = MaterialTheme.typography.titleMedium)
            Text(
                "Guarda una copia de la base de datos en el celular. Úsala antes de cambiar de equipo.",
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { vm.respaldar() }, modifier = Modifier.weight(1f)) {
                    Text("Respaldar ahora")
                }
                OutlinedButton(onClick = { confirmarRestaurar = true }, modifier = Modifier.weight(1f)) {
                    Text("Restaurar")
                }
            }

            vm.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }

    if (confirmarRestaurar) {
        AlertDialog(
            onDismissRequest = { confirmarRestaurar = false },
            title = { Text("¿Restaurar la última copia?") },
            text = { Text("Se reemplazarán TODOS los datos actuales por los de la copia. La app se cerrará.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarRestaurar = false
                    if (vm.restaurar()) {
                        // Cerramos la app para que al abrirla cargue la base de datos restaurada
                        (context as? Activity)?.finishAffinity()
                    }
                }) { Text("Restaurar") }
            },
            dismissButton = { TextButton(onClick = { confirmarRestaurar = false }) { Text("Cancelar") } }
        )
    }
}
