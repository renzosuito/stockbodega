package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object EstadoVenta {
    const val EN_CURSO = "EN_CURSO"
    const val EMITIDA = "EMITIDA"
    const val CANCELADA = "CANCELADA"
}

// EXPO: Cabecera de la venta. Solo Ticket o Boleta interna (sin factura ni SUNAT).
// El estado sigue el diagrama de estados: EnCurso -> Emitida o Cancelada.
@Entity(
    tableName = "venta",
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["idUsuario"],
            childColumns = ["idUsuario"]),
        ForeignKey(entity = ProvCliente::class, parentColumns = ["idProvCliente"],
            childColumns = ["idProvCliente"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("idUsuario"), Index("idProvCliente")]
)
data class Venta(
    @PrimaryKey(autoGenerate = true) val idVenta: Int = 0,
    val tipoComprobante: String,      // "BOLETA" o "TICKET"
    val serieNumero: String,          // p. ej. B001-00000123
    val fecha: Long,
    val metodoPago: String,           // EFECTIVO, YAPE, PLIN, TARJETA
    val total: Double,
    val estado: String = EstadoVenta.EN_CURSO,
    val idProvCliente: Int? = null,   // opcional: "Clientes Varios" si es null
    val idUsuario: Int                // vendedor que cobró
)