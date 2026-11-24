package com.example.stockbodega.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.model.Categoria
import com.example.stockbodega.data.model.Producto
import com.example.stockbodega.data.repository.ProductoRepository
import com.example.stockbodega.util.aDoubleONull
import com.example.stockbodega.util.aFechaONull
import com.example.stockbodega.util.fechaCorta
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// EXPO (RF-05 / P04): formulario para registrar o editar un producto.
// Regla importante: NO existe el campo "stock inicial". El stock se mueve solo por
// guías de internamiento (sube), ventas y mermas (bajan). Aquí solo se MUESTRA.
class ProductoFormViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductoRepository(AppDatabase.getDatabase(app))

    val categorias: StateFlow<List<Categoria>> = repo.categorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Campos del formulario (texto, porque así los escribe el usuario)
    var nombre by mutableStateOf("")
    var marca by mutableStateOf("")
    var codigo by mutableStateOf("")
    var idCategoria by mutableStateOf<Int?>(null)
    var precioCompra by mutableStateOf("")
    var precioVenta by mutableStateOf("")
    var stockMinimo by mutableStateOf("")
    var vencimiento by mutableStateOf("")           // dd/mm/aaaa, opcional
    var stockActual by mutableStateOf(0)            // solo lectura
    var error by mutableStateOf<String?>(null)

    private var original: Producto? = null          // el producto que se está editando
    val esEdicion: Boolean get() = original != null

    fun cargar(id: Int) {
        viewModelScope.launch {
            repo.obtener(id)?.let { p ->
                original = p
                nombre = p.nombre
                marca = p.marca
                codigo = p.codigoBarras
                idCategoria = p.idCategoria
                precioCompra = p.precioCompra.toString()
                precioVenta = p.precioVenta.toString()
                stockMinimo = p.stockMinimo.toString()
                vencimiento = p.fechaVencimiento?.fechaCorta() ?: ""
                stockActual = p.stockActual
            }
        }
    }

    fun guardar(onListo: () -> Unit) {
        val pc = precioCompra.aDoubleONull()
        val pv = precioVenta.aDoubleONull()
        val sm = stockMinimo.trim().toIntOrNull()
        val fechaVenc = if (vencimiento.isBlank()) null else vencimiento.aFechaONull()

        // Validaciones: el primer problema encontrado se muestra al usuario
        error = when {
            nombre.isBlank() -> "Escribe el nombre del producto"
            marca.isBlank() -> "Escribe la marca"
            codigo.isBlank() -> "Escribe o escanea el código"
            idCategoria == null -> "Elige una categoría"
            pc == null || pc < 0 -> "Precio de compra no válido"
            pv == null || pv <= 0 -> "Precio de venta no válido"
            sm == null || sm < 0 -> "Stock mínimo no válido"
            vencimiento.isNotBlank() && fechaVenc == null -> "Fecha de vencimiento no válida (dd/mm/aaaa)"
            else -> null
        }
        if (error != null) return

        val base = original
        val producto = Producto(
            idProducto = base?.idProducto ?: 0,
            codigoBarras = codigo.trim(),
            nombre = nombre.trim(),
            marca = marca.trim(),
            idCategoria = idCategoria!!,
            precioCompra = pc!!,
            precioVenta = pv!!,
            stockActual = base?.stockActual ?: 0,       // se conserva; nunca se escribe a mano
            stockMinimo = sm!!,
            fechaVencimiento = fechaVenc,
            activo = true
        )

        viewModelScope.launch {
            repo.guardar(producto)
                .onSuccess { onListo() }
                .onFailure { error = it.message }
        }
    }

    fun darDeBaja(onListo: () -> Unit) {
        val p = original ?: return
        viewModelScope.launch {
            repo.darDeBaja(p)
            onListo()
        }
    }
}