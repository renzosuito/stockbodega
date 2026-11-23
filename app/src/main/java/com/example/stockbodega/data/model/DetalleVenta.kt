package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// EXPO: Este es el "Detalle de venta" (antes "carrito"). El precio se
// congela al momento del cobro: si el precio del producto cambia después,
// las ventas pasadas no se alteran.
@Entity(
    tableName = "detalle_venta",
    foreignKeys = [
        ForeignKey(entity = Venta::class, parentColumns = ["idVenta"],
            childColumns = ["idVenta"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Producto::class, parentColumns = ["idProducto"],
            childColumns = ["idProducto"])
    ],
    indices = [Index("idVenta"), Index("idProducto")]
)
data class DetalleVenta(
    @PrimaryKey(autoGenerate = true) val idDetalleVenta: Int = 0,
    val idVenta: Int,
    val idProducto: Int,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)