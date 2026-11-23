package com.example.stockbodega.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.Sesion
import com.example.stockbodega.data.model.Rol
import com.example.stockbodega.data.model.Usuario
import com.example.stockbodega.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuariosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UsuarioRepository(AppDatabase.getDatabase(app))

    // Lista que se actualiza sola cuando se agrega o cambia un usuario
    val usuarios: StateFlow<List<Usuario>> = repo.listar()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del cuadro "Nuevo usuario"
    var mostrarForm by mutableStateOf(false)
    var nombre by mutableStateOf("")
    var username by mutableStateOf("")
    var clave by mutableStateOf("")
    var rol by mutableStateOf(Rol.VENDEDOR)
    var error by mutableStateOf<String?>(null)

    fun abrirForm() {
        nombre = ""; username = ""; clave = ""; rol = Rol.VENDEDOR; error = null
        mostrarForm = true
    }

    fun guardar() {
        viewModelScope.launch {
            repo.crear(nombre, username, clave, rol)
                .onSuccess { mostrarForm = false }
                .onFailure { error = it.message }
        }
    }

    fun alternarActivo(usuario: Usuario) {
        // Regla: el administrador no puede desactivarse a sí mismo (se quedaría sin acceso)
        if (usuario.idUsuario == Sesion.usuario?.idUsuario) return
        viewModelScope.launch { repo.cambiarActivo(usuario) }
    }
}
