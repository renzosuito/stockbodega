package com.example.stockbodega.data.repository

import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.model.Categoria
import com.example.stockbodega.data.model.Producto
import kotlinx.coroutines.flow.Flow

// EXPO: Reglas del catálogo. Por ejemplo: el código de barras no se puede repetir
// (la tabla tiene un índice único) y "eliminar" un producto en realidad lo DESACTIVA,
// para no perder su historial de ventas y kardex.
class ProductoRepository(db: AppDatabase) {
    private val productoDao = db.productoDao()
    private val categoriaDao = db.categoriaDao()

    fun buscar(texto: String): Flow<List<Producto>> = productoDao.buscar(texto)
    fun categorias(): Flow<List<Categoria>> = categoriaDao.listar()
    suspend fun obtener(id: Int): Producto? = productoDao.obtenerPorId(id)

    /** Crea o actualiza. Si el código ya existe devuelve un error entendible. */
    suspend fun guardar(producto: Producto): Result<Unit> = try {
        if (producto.idProducto == 0) productoDao.insertar(producto) else productoDao.actualizar(producto)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(IllegalArgumentException("No se pudo guardar. ¿El código ya existe en otro producto?"))
    }

    suspend fun darDeBaja(producto: Producto) {
        productoDao.actualizar(producto.copy(activo = false))
    }
}