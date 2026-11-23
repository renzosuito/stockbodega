package com.example.stockbodega.data

import com.example.stockbodega.data.model.Rol
import com.example.stockbodega.data.model.Usuario

// EXPO: "Quién está usando la app ahora". Se llena al iniciar sesión y se borra al cerrarla.
// Con esto aplicamos el CONTROL DE ROLES: el Administrador ve todo; el Vendedor solo
// ventas, consulta de productos y kardex.
object Sesion {
    var usuario: Usuario? = null
    val esAdmin: Boolean
        get() = usuario?.rol == Rol.ADMINISTRADOR

    fun cerrar() {
        usuario = null
    }
}