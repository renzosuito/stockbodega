package com.example.stockbodega.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.RespaldoHelper
import com.example.stockbodega.data.model.Bodega
import com.example.stockbodega.data.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// RF-03 (datos de la bodega) y RF-17 (respaldo y restauración)
class ConfiguracionViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repo = UsuarioRepository(AppDatabase.getDatabase(app))
    private var bodegaActual: Bodega? = null

    var nombreComercial by mutableStateOf("")
    var ruc by mutableStateOf("")
    var direccion by mutableStateOf("")
    var telefono by mutableStateOf("")
    var mensaje by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            repo.obtenerBodega()?.let {
                bodegaActual = it
                nombreComercial = it.nombreComercial
                ruc = it.ruc
                direccion = it.direccion
                telefono = it.telefono
            }
        }
    }

    fun guardar() {
        if (nombreComercial.isBlank()) {
            mensaje = "El nombre comercial es obligatorio"
            return
        }
        viewModelScope.launch {
            // Si ya había bodega se conserva su id para REEMPLAZAR la fila (no crear otra)
            repo.guardarBodega(
                Bodega(
                    idBodega = bodegaActual?.idBodega ?: 0,
                    nombreComercial = nombreComercial.trim(),
                    ruc = ruc.trim(),
                    direccion = direccion.trim(),
                    telefono = telefono.trim()
                )
            )
            mensaje = "Datos de la bodega guardados"
        }
    }

    fun respaldar() {
        // Dispatchers.IO = hilo para operaciones con archivos (no bloquea la pantalla)
        // El archivo se copia en segundo plano; el mensaje se muestra de vuelta en el hilo principal.
        viewModelScope.launch {
            mensaje = withContext(Dispatchers.IO) { runCatching { RespaldoHelper.respaldar(app) } }
                .fold(
                    onSuccess = { "Respaldo creado: ${it.name}" },
                    onFailure = { "No se pudo respaldar: ${it.message}" }
                )
        }
    }

    /** Devuelve true si se restauró; la pantalla debe cerrar la app para que cargue la BD nueva. */
    fun restaurar(): Boolean {
        val ok = runCatching { RespaldoHelper.restaurar(app) }.getOrDefault(false)
        mensaje = if (ok) "Copia restaurada. La app se cerrará; ábrela de nuevo."
        else "No hay copias de seguridad para restaurar"
        return ok
    }
}
