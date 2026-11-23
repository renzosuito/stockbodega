package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// EXPO: Es la tabla central. Fíjense que NO hay "stock inicial" editable:
// stockActual solo cambia por guías de ingreso (sube) y por ventas/mermas (baja).
// Si stockActual <= stockMinimo, el producto genera una alerta (RF-11).
@Entity(
    tableName = "producto",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["idCategoria"],
        childColumns = ["idCategoria"],
        onDelete = ForeignKey.RESTRICT   // no se borra una categoría con productos
    )],
    indices = [
        Index("idCategoria"),
        Index(value = ["codigoBarras"], unique = true)
    ]
)
data class Producto(
    @PrimaryKey(autoGenerate = true) val idProducto: Int = 0,
    val codigoBarras: String,
    val nombre: String,
    val marca: String,
    val idCategoria: Int,
    val precioCompra: Double,
    val precioVenta: Double,
    val stockActual: Int = 0,
    val stockMinimo: Int = 0,
    val fechaVencimiento: Long? = null,   // milisegundos; null = no vence
    val activo: Boolean = true
)