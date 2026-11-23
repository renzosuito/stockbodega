package com.example.stockbodega.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stockbodega.data.model.*

// EXPO: @Database junta todas las tablas en un solo archivo SQLite
// ("stockbodega.db") que vive dentro del celular. 100% offline.
// version = 1: si cambiamos las tablas, hay que subir este número.
@Database(
    entities = [
        Bodega::class, Categoria::class, Usuario::class, ProvCliente::class,
        Producto::class, DocumentoAlmacen::class, DetalleDocAlmacen::class,
        Venta::class, DetalleVenta::class, MovimientoKardex::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // En el Paso 2 aquí agregamos un DAO por módulo, por ejemplo:
    // abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Singleton: toda la app usa UNA sola instancia de la base de datos.
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stockbodega.db"
                ).build().also { INSTANCE = it }
            }
    }
}