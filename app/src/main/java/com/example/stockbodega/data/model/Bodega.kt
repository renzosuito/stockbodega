package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// EXPO: Una Entity es una clase Kotlin que Room convierte en una tabla SQLite.
// "bodega" guarda los datos fijos del negocio (se imprimen en el ticket).
@Entity(tableName = "bodega")
data class Bodega(
    @PrimaryKey(autoGenerate = true) val idBodega: Int = 0,
    val nombreComercial: String,
    val ruc: String = "",
    val direccion: String = "",
    val telefono: String = ""
)