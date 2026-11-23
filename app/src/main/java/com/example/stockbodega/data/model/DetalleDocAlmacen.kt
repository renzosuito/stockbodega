package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Renglones de la guía: producto + cantidad. Nada más.
@Entity(
    tableName = "detalle_doc_almacen",
    foreignKeys = [
        ForeignKey(entity = DocumentoAlmacen::class, parentColumns = ["idDocumento"],
            childColumns = ["idDocumento"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Producto::class, parentColumns = ["idProducto"],
            childColumns = ["idProducto"])
    ],
    indices = [Index("idDocumento"), Index("idProducto")]
)
data class DetalleDocAlmacen(
    @PrimaryKey(autoGenerate = true) val idDetalleDoc: Int = 0,
    val idDocumento: Int,
    val idProducto: Int,
    val cantidad: Int
)