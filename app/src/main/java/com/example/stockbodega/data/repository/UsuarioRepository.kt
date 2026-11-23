package com.example.stockbodega.data.repository

import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.model.Bodega
import com.example.stockbodega.data.model.Usuario
import com.example.stockbodega.util.Seguridad
import kotlinx.coroutines.flow.Flow

// EXPO: El Repository es el "intermediario" entre las pantallas y la base de datos.
// La pantalla nunca habla con SQL: le pide cosas al Repository ("autenticar", "crear usuario")
// y aquí se aplican las reglas del negocio (clave mínima, usuario no repetido...).
class UsuarioRepository(db: AppDatabase) {

    private val usuarioDao = db.usuarioDao()
    private val bodegaDao = db.bodegaDao()

    /** Devuelve el usuario si el usuario/clave son correctos Y la cuenta está activa; si no, null. */
    suspend fun autenticar(username: String, clave: String): Usuario? {
        val usuario = usuarioDao.buscarPorUsername(username) ?: return null
        if (!usuario.activo) return null
        return if (Seguridad.verificar(clave, usuario.claveHash)) usuario else null
    }

    fun listar(): Flow<List<Usuario>> = usuarioDao.listar()

    suspend fun crear(nombre: String, username: String, clave: String, rol: String): Result<Unit> {
        if (nombre.isBlank() || username.isBlank()) {
            return Result.failure(IllegalArgumentException("Completa nombre y usuario"))
        }
        if (clave.length < 8) {
            return Result.failure(IllegalArgumentException("La clave debe tener mínimo 8 caracteres"))
        }
        if (usuarioDao.buscarPorUsername(username.trim()) != null) {
            return Result.failure(IllegalArgumentException("Ese usuario ya existe"))
        }
        usuarioDao.insertar(
            Usuario(
                nombreCompleto = nombre.trim(),
                username = username.trim(),
                claveHash = Seguridad.hash(clave),   // se guarda el hash, nunca la clave
                rol = rol,
                activo = true
            )
        )
        return Result.success(Unit)
    }

    suspend fun cambiarActivo(usuario: Usuario) {
        usuarioDao.actualizar(usuario.copy(activo = !usuario.activo))
    }

    // ---- Datos de la bodega (pantalla Configuración) ----
    suspend fun obtenerBodega(): Bodega? = bodegaDao.obtener()

    suspend fun guardarBodega(bodega: Bodega) {
        bodegaDao.guardar(bodega)
    }
}
