package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockbodega.data.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categoria ORDER BY nombre")
    fun listar(): Flow<List<Categoria>>

    @Insert
    suspend fun insertar(categoria: Categoria): Long
}