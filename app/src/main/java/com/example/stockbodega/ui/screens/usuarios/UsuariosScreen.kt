package com.example.stockbodega.ui.screens.usuarios

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.stockbodega.data.Sesion
import com.example.stockbodega.data.model.Rol
import com.example.stockbodega.ui.components.EstadoVacio
import com.example.stockbodega.ui.components.SelectorDesplegable
import com.example.stockbodega.ui.components.StockTopBar
import com.example.stockbodega.viewmodel.UsuariosViewModel

// EXPO (RF-02): solo el ADMINISTRADOR llega a esta pantalla (se oculta para el Vendedor).
// Permite crear usuarios, elegir su rol y activarlos o desactivarlos.
@Composable
fun UsuariosScreen(
    onVolver: () -> Unit,
    vm: UsuariosViewModel = viewModel()
) {
    val usuarios by vm.usuarios.collectAsState()

    Scaffold(
        topBar = { StockTopBar("Usuarios", onVolver) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { vm.abrirForm() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo usuario") }
            )
        }
    ) { padding ->
        if (usuarios.isEmpty()) {
            EstadoVacio("No hay usuarios registrados")
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(usuarios, key = { it.idUsuario }) { u ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(u.nombreCompleto, fontWeight = FontWeight.Bold)
                                Text(
                                    "@${u.username} · ${u.rol.lowercase().replaceFirstChar { c -> c.uppercase() }}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(if (u.activo) "Activo" else "Inactivo", style = MaterialTheme.typography.bodySmall)
                            Switch(
                                checked = u.activo,
                                onCheckedChange = { vm.alternarActivo(u) },
                                // No puedes desactivar tu propia cuenta
                                enabled = u.idUsuario != Sesion.usuario?.idUsuario
                            )
                        }
                    }
                }
            }
        }
    }

    // Ventana emergente para crear un usuario
    if (vm.mostrarForm) {
        AlertDialog(
            onDismissRequest = { vm.mostrarForm = false },
            title = { Text("Nuevo usuario") },
            text = {
                Column {
                    OutlinedTextField(vm.nombre, { vm.nombre = it }, label = { Text("Nombre completo") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(vm.username, { vm.username = it }, label = { Text("Usuario") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        vm.clave, { vm.clave = it },
                        label = { Text("Contraseña (mín. 8)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(8.dp))
                    SelectorDesplegable(
                        etiqueta = "Rol",
                        opciones = listOf("Administrador", "Vendedor"),
                        seleccion = if (vm.rol == Rol.ADMINISTRADOR) "Administrador" else "Vendedor",
                        onSeleccion = { vm.rol = if (it == 0) Rol.ADMINISTRADOR else Rol.VENDEDOR }
                    )
                    vm.error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { vm.guardar() }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { vm.mostrarForm = false }) { Text("Cancelar") } }
        )
    }
}
