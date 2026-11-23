package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.stockbodega.data.model.ProvCliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ProvClienteDao {
    @Query(
        "SELECT * FROM prov_cliente WHERE " +
                "nombreRazonSocial LIKE '%' || :texto || '%' OR " +
                "numDocumento LIKE '%' || :texto || '%' ORDER BY nombreRazonSocial"
    )
    fun buscar(texto: String): Flow<List<ProvCliente>>

    @Insert
    suspend fun insertar(provCliente: ProvCliente): Long

    @Update
    suspend fun actualizar(provCliente: ProvCliente)
}