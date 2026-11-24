package com.example.stockbodega.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockbodega.data.AppDatabase
import com.example.stockbodega.data.model.Categoria
import com.example.stockbodega.data.model.Producto
import com.example.stockbodega.data.repository.ProductoRepository
import com.example.stockbodega.util.DIA_MS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

enum class FiltroStock(val etiqueta: String) {
    TODOS("Todos"),
    BAJO("Stock bajo"),
    SIN_STOCK("Sin stock"),
    POR_VENCER("Por vencer")
}

// EXPO: Aquí se combinan DOS cosas que cambian: lo que el usuario escribe en el buscador
// y el filtro elegido (chips). Cada vez que cambia una, la lista se recalcula sola.
@OptIn(ExperimentalCoroutinesApi::class)
class ProductosViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductoRepository(AppDatabase.getDatabase(app))

    val texto = MutableStateFlow("")
    val filtro = MutableStateFlow(FiltroStock.TODOS)

    val categorias: StateFlow<List<Categoria>> = repo.categorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productos: StateFlow<List<Producto>> = texto
        .flatMapLatest { repo.buscar(it) }               // 1) busca en la BD
        .combine(filtro) { lista, f -> aplicarFiltro(lista, f) }   // 2) aplica el chip elegido
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarTexto(nuevo: String) {
        texto.value = nuevo
    }

    fun cambiarFiltro(nuevo: FiltroStock) {
        filtro.value = nuevo
    }

    private fun aplicarFiltro(lista: List<Producto>, filtro: FiltroStock): List<Producto> {
        val limiteVencimiento = System.currentTimeMillis() + 7 * DIA_MS   // vence en 7 días o menos
        return when (filtro) {
            FiltroStock.TODOS -> lista
            FiltroStock.BAJO -> lista.filter { it.stockActual <= it.stockMinimo }
            FiltroStock.SIN_STOCK -> lista.filter { it.stockActual == 0 }
            FiltroStock.POR_VENCER -> lista.filter {
                it.fechaVencimiento != null && it.fechaVencimiento <= limiteVencimiento
            }
        }
    }
}