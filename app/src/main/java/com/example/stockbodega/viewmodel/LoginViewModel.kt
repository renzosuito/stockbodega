package com.example.stockbodega.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.Sesion
import com.example.stockbodega.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

// EXPO: El ViewModel guarda el ESTADO de la pantalla (lo que escribió el usuario, el mensaje
// de error) y sobrevive a girar el celular. La pantalla solo "dibuja" ese estado.
// Patrón MVVM: Pantalla (Vista) -> ViewModel -> Repository -> DAO -> SQLite.
class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UsuarioRepository(AppDatabase.getDatabase(app))

    // mutableStateOf: cuando el valor cambia, Compose vuelve a dibujar la pantalla solo.
    var usuario by mutableStateOf("")
    var clave by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var cargando by mutableStateOf(false)

    fun ingresar(onExito: () -> Unit) {
        if (usuario.isBlank() || clave.isBlank()) {
            error = "Completa usuario y contraseña"
            return
        }
        viewModelScope.launch {
            cargando = true
            val encontrado = repo.autenticar(usuario.trim(), clave)
            cargando = false
            if (encontrado == null) {
                error = "Usuario o contraseña incorrectos"
            } else {
                error = null
                Sesion.usuario = encontrado   // guardamos quién entró (y su rol)
                clave = ""
                onExito()
            }
        }
    }
}
