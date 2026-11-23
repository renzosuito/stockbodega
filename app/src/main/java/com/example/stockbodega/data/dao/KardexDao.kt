package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockbodega.data.model.MovimientoKardex
import kotlinx.coroutines.flow.Flow

@Dao
interface KardexDao {
    @Insert
    suspend fun insertar(movimiento: MovimientoKardex)

    // Historial de UN producto, de más antiguo a más nuevo (así se lee el saldo como un libro)
    @Query(
        "SELECT * FROM movimiento_kardex WHERE idProducto = :idProducto " +
                "AND fecha BETWEEN :desde AND :hasta ORDER BY fecha ASC, idKardex ASC"
    )
    fun movimientos(idProducto: Int, desde: Long, hasta: Long): Flow<List<MovimientoKardex>>
}