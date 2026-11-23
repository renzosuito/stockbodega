package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockbodega.data.model.DetalleDocAlmacen
import com.example.stockbodega.data.model.DocumentoAlmacen
import kotlinx.coroutines.flow.Flow

// Resultado de una consulta con JOIN: una fila "resumen" por cada guía.
// Room llena estas clases usando los nombres "AS" del SELECT.
data class ResumenGuia(
    val idDocumento: Int,
    val numeroGuia: String,
    val fecha: Long,
    val motivo: String,
    val productos: Int,
    val unidades: Int
)

data class ItemGuia(
    val nombre: String,
    val cantidad: Int
)

@Dao
interface AlmacenDao {
    @Insert
    suspend fun insertarDocumento(documento: DocumentoAlmacen): Long

    @Insert
    suspend fun insertarDetalles(detalles: List<DetalleDocAlmacen>)

    // Sirve para numerar: si ya hay 4 guías de ingreso, la siguiente es GI-0005
    @Query("SELECT COUNT(*) FROM documento_almacen WHERE tipoOperacion = :tipo")
    suspend fun contar(tipo: String): Int

    // EXPO: JOIN = unir tablas. Aquí juntamos la guía con sus renglones para contar
    // cuántos productos y cuántas unidades trae, sin repetir datos.
    @Query(
        "SELECT d.idDocumento AS idDocumento, d.numeroGuia AS numeroGuia, d.fecha AS fecha, " +
                "d.motivo AS motivo, COUNT(x.idDetalleDoc) AS productos, " +
                "COALESCE(SUM(x.cantidad), 0) AS unidades " +
                "FROM documento_almacen d LEFT JOIN detalle_doc_almacen x ON x.idDocumento = d.idDocumento " +
                "WHERE d.tipoOperacion = :tipo AND d.fecha BETWEEN :desde AND :hasta " +
                "GROUP BY d.idDocumento ORDER BY d.fecha DESC"
    )
    fun resumenes(tipo: String, desde: Long, hasta: Long): Flow<List<ResumenGuia>>

    @Query(
        "SELECT p.nombre AS nombre, x.cantidad AS cantidad FROM detalle_doc_almacen x " +
                "INNER JOIN producto p ON p.idProducto = x.idProducto WHERE x.idDocumento = :idDocumento"
    )
    suspend fun detallesDe(idDocumento: Int): List<ItemGuia>
}