package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.stockbodega.data.model.Usuario
import kotlinx.coroutines.flow.Flow

// EXPO: Un DAO ("Data Access Object") es una interfaz donde escribimos las consultas SQL.
// Room genera el código por nosotros. "suspend" = se ejecuta en segundo plano sin congelar la pantalla.
// "Flow" = lista que se ACTUALIZA SOLA en pantalla cuando cambia la tabla.
@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Update
    suspend fun actualizar(usuario: Usuario)

    // Para el login: busca un usuario por su nombre de usuario
    @Query("SELECT * FROM usuario WHERE username = :username LIMIT 1")
    suspend fun buscarPorUsername(username: String): Usuario?

    @Query("SELECT * FROM usuario ORDER BY nombreCompleto")
    fun listar(): Flow<List<Usuario>>
}