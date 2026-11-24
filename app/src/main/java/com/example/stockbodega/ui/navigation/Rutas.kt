package com.example.stockbodega.ui.navigation

// EXPO: Cada pantalla tiene un "nombre de ruta" (como la dirección de una página web).
// Navegar = decirle al NavController a qué ruta ir. Todas están en un solo lugar.
object Rutas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val PRODUCTOS = "productos"
    const val PRODUCTO_FORM = "producto_form/{id}"      // {id} = número del producto (0 = nuevo)
    fun productoForm(id: Int) = "producto_form/$id"
    const val VENTAS = "ventas"                          // historial de ventas
    const val DETALLE_VENTA = "detalle_venta"
    const val CONFIRMAR_VENTA = "confirmar_venta"
    const val VENTA_REGISTRADA = "venta_registrada"
    const val ALMACEN = "almacen"                        // historial de guías
    const val GUIA_NUEVA = "guia_nueva"
    const val MERMA = "merma"
    const val KARDEX = "kardex"
    const val PROVCLIENTE = "provcliente"
    const val REPORTES = "reportes"
    const val USUARIOS = "usuarios"
    const val CONFIG = "configuracion"
    const val MAS = "mas"
}