package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stockbodega.data.model.DetalleVenta
import com.example.stockbodega.data.model.Venta
import kotlinx.coroutines.flow.Flow

// Un renglón del detalle de venta, con el nombre del producto (viene de un JOIN)
data class ItemVentaVista(
    val nombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

@Dao
interface VentaDao {
    @Insert
    suspend fun insertarVenta(venta: Venta): Long

    @Insert
    suspend fun insertarDetalles(detalles: List<DetalleVenta>)

    // Para numerar el comprobante: B001-00000001, B001-00000002...
    @Query("SELECT COUNT(*) FROM venta WHERE tipoComprobante = :tipo")
    suspend fun contar(tipo: String): Int

    // idUsuario = 0 significa "todas las ventas" (Administrador).
    // Un Vendedor pasa su propio id y solo ve sus ventas del turno.
    @Query(
        "SELECT * FROM venta WHERE estado = 'EMITIDA' AND fecha BETWEEN :desde AND :hasta " +
                "AND (:idUsuario = 0 OR idUsuario = :idUsuario) ORDER BY fecha DESC"
    )
    fun listar(desde: Long, hasta: Long, idUsuario: Int): Flow<List<Venta>>

    @Query(
        "SELECT p.nombre AS nombre, d.cantidad AS cantidad, d.precioUnitario AS precioUnitario, " +
                "d.subtotal AS subtotal FROM detalle_venta d " +
                "INNER JOIN producto p ON p.idProducto = d.idProducto WHERE d.idVenta = :idVenta"
    )
    suspend fun detalleDe(idVenta: Int): List<ItemVentaVista>

    // Para el Inicio: total vendido y número de ventas en un periodo
    @Query(
        "SELECT COALESCE(SUM(total), 0) FROM venta " +
                "WHERE estado = 'EMITIDA' AND fecha BETWEEN :desde AND :hasta"
    )
    fun totalPeriodo(desde: Long, hasta: Long): Flow<Double>

    @Query(
        "SELECT COUNT(*) FROM venta WHERE estado = 'EMITIDA' AND fecha BETWEEN :desde AND :hasta"
    )
    fun cantidadPeriodo(desde: Long, hasta: Long): Flow<Int>
}