package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TipoOperacion {
    const val INGRESO = "INGRESO"   // Guía de internamiento
    const val MERMA = "MERMA"       // Guía de salida por merma
}

// EXPO: Una sola tabla para los dos documentos de almacén. La guía de
// internamiento (GI-0001) solo registra CANTIDADES: sin proveedor, factura ni precios.
@Entity(
    tableName = "documento_almacen",
    foreignKeys = [ForeignKey(
        entity = Usuario::class,
        parentColumns = ["idUsuario"],
        childColumns = ["idUsuario"]
    )],
    indices = [Index("idUsuario")]
)
data class DocumentoAlmacen(
    @PrimaryKey(autoGenerate = true) val idDocumento: Int = 0,
    val tipoOperacion: String,        // TipoOperacion.INGRESO o MERMA
    val numeroGuia: String,           // p. ej. GI-0001 o GS-0001
    val fecha: Long,
    val motivo: String = "",          // solo se usa en merma
    val idUsuario: Int                // quién la registró
)