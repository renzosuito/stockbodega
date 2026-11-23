package com.example.stockbodega.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class VentaDia(val dia: String, val total: Double)
data class ProductoTop(val nombre: String, val unidades: Int)

// EXPO (RF-15): los reportes NO tienen tabla propia. Son consultas que resumen
// (SUM, COUNT, GROUP BY) los datos de ventas e inventario que ya existen.
@Dao
interface ReportesDao {
    // Total vendido por día. strftime convierte los milisegundos en "2026-05-24".
    @Query(
        "SELECT strftime('%Y-%m-%d', fecha / 1000, 'unixepoch', 'localtime') AS dia, " +
                "SUM(total) AS total FROM venta " +
                "WHERE estado = 'EMITIDA' AND fecha BETWEEN :desde AND :hasta GROUP BY dia ORDER BY dia"
    )
    fun ventasPorDia(desde: Long, hasta: Long): Flow<List<VentaDia>>

    // Los 5 productos que más unidades vendieron
    @Query(
        "SELECT p.nombre AS nombre, SUM(d.cantidad) AS unidades FROM detalle_venta d " +
                "INNER JOIN venta v ON v.idVenta = d.idVenta " +
                "INNER JOIN producto p ON p.idProducto = d.idProducto " +
                "WHERE v.estado = 'EMITIDA' AND v.fecha BETWEEN :desde AND :hasta " +
                "GROUP BY d.idProducto ORDER BY unidades DESC LIMIT 5"
    )
    fun topProductos(desde: Long, hasta: Long): Flow<List<ProductoTop>>

    // Valor del inventario a precio de compra: stock x precio de compra de cada producto
    @Query("SELECT COALESCE(SUM(stockActual * precioCompra), 0) FROM producto WHERE activo = 1")
    fun valorInventario(): Flow<Double>
}