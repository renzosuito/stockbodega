package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Clasifica los productos (abarrotes, lácteos, bebidas...).
@Entity(tableName = "categoria")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val idCategoria: Int = 0,
    val nombre: String,
    val descripcion: String = ""
)