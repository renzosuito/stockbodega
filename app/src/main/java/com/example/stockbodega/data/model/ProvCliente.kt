package com.example.stockbodega.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// EXPO: Tabla unificada (recomendación del profesor): clientes y proveedores
// en una sola tabla. Es un DIRECTORIO; no se vincula a la guía de internamiento.
@Entity(tableName = "prov_cliente")
data class ProvCliente(
    @PrimaryKey(autoGenerate = true) val idProvCliente: Int = 0,
    val numDocumento: String,        // DNI o RUC
    val nombreRazonSocial: String,
    val telefono: String = "",
    val direccion: String = "",
    val esCliente: Boolean = true,
    val esProveedor: Boolean = false,
    val esEmpresa: Boolean = false   // distingue persona natural de empresa
)