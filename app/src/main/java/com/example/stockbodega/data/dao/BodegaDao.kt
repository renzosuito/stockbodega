package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stockbodega.data.model.Bodega
import kotlinx.coroutines.flow.Flow

@Dao
interface BodegaDao {
    // Solo existe UNA bodega (la del negocio): tomamos la primera fila
    @Query("SELECT * FROM bodega LIMIT 1")
    fun observar(): Flow<Bodega?>

    @Query("SELECT * FROM bodega LIMIT 1")
    suspend fun obtener(): Bodega?

    // REPLACE: si ya existe esa fila (mismo id) la reemplaza; si no, la crea
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(bodega: Bodega): Long
}