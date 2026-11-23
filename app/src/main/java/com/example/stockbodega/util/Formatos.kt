package com.example.stockbodega.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// EXPO: Funciones pequeñas que usan TODAS las pantallas: mostrar dinero (S/ 12.50),
// fechas (24/05/2026) y rangos de tiempo (hoy, esta semana, este mes).
// Así nadie repite código y todos los montos se ven igual.
const val DIA_MS = 24L * 60 * 60 * 1000

/** 12.5 -> "S/ 12.50" */
fun Double.soles(): String = "S/ " + String.format(Locale.US, "%.2f", this)

/** Milisegundos -> "24/05/2026" */
fun Long.fechaCorta(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))

/** Milisegundos -> "24/05/2026 10:30" */
fun Long.fechaHora(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(this))

/** "12,5" o "12.5" -> 12.5 ; si no es un número devuelve null */
fun String.aDoubleONull(): Double? = replace(",", ".").trim().toDoubleOrNull()

/** "24/05/2026" -> milisegundos ; si el formato es inválido devuelve null */
fun String.aFechaONull(): Long? = try {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formato.isLenient = false          // rechaza fechas imposibles como 31/02/2026
    formato.parse(this.trim())?.time
} catch (e: Exception) {
    null
}

/** Milisegundos de las 00:00:00 del día indicado. */
fun inicioDelDia(ms: Long = System.currentTimeMillis()): Long {
    val c = Calendar.getInstance()
    c.timeInMillis = ms
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.timeInMillis
}

/** Milisegundos de las 23:59:59 del día indicado. */
fun finDelDia(ms: Long = System.currentTimeMillis()): Long = inicioDelDia(ms) + DIA_MS - 1

// Periodos que se eligen con las "chips" de filtro (Hoy / Esta semana / Este mes / Todo).
enum class Periodo(val etiqueta: String) {
    HOY("Hoy"),
    SEMANA("Esta semana"),
    MES("Este mes"),
    TODO("Todo")
}

/** Devuelve (desde, hasta) en milisegundos para el periodo elegido. */
fun rangoDe(periodo: Periodo): Pair<Long, Long> {
    val ahora = System.currentTimeMillis()
    return when (periodo) {
        Periodo.HOY -> inicioDelDia(ahora) to finDelDia(ahora)
        Periodo.SEMANA -> inicioDelDia(ahora - 6 * DIA_MS) to finDelDia(ahora)
        Periodo.MES -> {
            val c = Calendar.getInstance()
            c.set(Calendar.DAY_OF_MONTH, 1)
            inicioDelDia(c.timeInMillis) to finDelDia(ahora)
        }
        Periodo.TODO -> 0L to Long.MAX_VALUE
    }
}