package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Roles del sistema (control de acceso). Solo hay 2, como en el documento.
object Rol {
    const val ADMINISTRADOR = "ADMINISTRADOR"
    const val VENDEDOR = "VENDEDOR"
}

// EXPO: El login usa esta tabla. La clave NO se guarda en texto plano
// sino como hash (RNF-09). El campo "rol" decide qué pantallas ve cada uno.
@Entity(
    tableName = "usuario",
    indices = [Index(value = ["username"], unique = true)] // no se repite el usuario
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val idUsuario: Int = 0,
    val nombreCompleto: String,
    val username: String,
    val claveHash: String,
    val rol: String,            // Rol.ADMINISTRADOR o Rol.VENDEDOR
    val activo: Boolean = true
)