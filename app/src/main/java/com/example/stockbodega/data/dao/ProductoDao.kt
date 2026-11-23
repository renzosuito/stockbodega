package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.stockbodega.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    // Buscador: por nombre, marca o código. El "||" une textos en SQL (LIKE '%arroz%').
    @Query(
        "SELECT * FROM producto WHERE activo = 1 AND (" +
                "nombre LIKE '%' || :texto || '%' OR " +
                "marca LIKE '%' || :texto || '%' OR " +
                "codigoBarras LIKE '%' || :texto || '%') ORDER BY nombre"
    )
    fun buscar(texto: String): Flow<List<Producto>>

    @Query("SELECT * FROM producto WHERE idProducto = :id")
    suspend fun obtenerPorId(id: Int): Producto?

    @Query("SELECT * FROM producto WHERE codigoBarras = :codigo LIMIT 1")
    suspend fun obtenerPorCodigo(codigo: String): Producto?

    @Insert
    suspend fun insertar(producto: Producto): Long

    @Update
    suspend fun actualizar(producto: Producto)

    // EXPO: ÚNICA forma de cambiar el stock. Suma (ingreso) o resta (venta/merma).
    // Se llama siempre dentro de una transacción junto con el registro del kardex.
    @Query("UPDATE producto SET stockActual = stockActual + :delta WHERE idProducto = :id")
    suspend fun ajustarStock(id: Int, delta: Int)

    @Query("SELECT stockActual FROM producto WHERE idProducto = :id")
    suspend fun stockDe(id: Int): Int

    // RF-11 Alertas: stock menor o igual al mínimo
    @Query("SELECT * FROM producto WHERE activo = 1 AND stockActual <= stockMinimo ORDER BY stockActual")
    fun stockBajo(): Flow<List<Producto>>

    // RF-11 Alertas: productos que vencen antes de la fecha límite
    @Query(
        "SELECT * FROM producto WHERE activo = 1 AND fechaVencimiento IS NOT NULL " +
                "AND fechaVencimiento <= :limite ORDER BY fechaVencimiento"
    )
    fun porVencer(limite: Long): Flow<List<Producto>>
}