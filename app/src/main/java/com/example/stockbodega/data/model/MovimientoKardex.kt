package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TipoMovimiento {
    const val ENTRADA = "ENTRADA"   // guía de internamiento
    const val SALIDA = "SALIDA"     // venta
    const val MERMA = "MERMA"       // baja por merma
}

// EXPO: El kardex es el historial de cada producto. Nadie lo edita a mano:
// se escribe solo cada vez que entra, sale o se pierde mercadería.
// "saldoResultante" es cuánto queda en stock después del movimiento.
@Entity(
    tableName = "movimiento_kardex",
    foreignKeys = [ForeignKey(
        entity = Producto::class,
        parentColumns = ["idProducto"],
        childColumns = ["idProducto"]
    )],
    indices = [Index("idProducto")]
)
data class MovimientoKardex(
    @PrimaryKey(autoGenerate = true) val idKardex: Int = 0,
    val idProducto: Int,
    val fecha: Long,
    val tipoMovimiento: String,
    val cantidad: Int,
    val saldoResultante: Int,
    val referenciaDocumento: String   // p. ej. GI-0001 o B001-00000123
)