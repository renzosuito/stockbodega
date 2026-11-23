package com.example.stockbodega.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stockbodega.data.dao.AlmacenDao
import com.example.stockbodega.data.dao.BodegaDao
import com.example.stockbodega.data.dao.CategoriaDao
import com.example.stockbodega.data.dao.KardexDao
import com.example.stockbodega.data.dao.ProductoDao
import com.example.stockbodega.data.dao.ProvClienteDao
import com.example.stockbodega.data.dao.ReportesDao
import com.example.stockbodega.data.dao.UsuarioDao
import com.example.stockbodega.data.dao.VentaDao
import com.example.stockbodega.data.model.*
import com.example.stockbodega.util.Seguridad

// EXPO: @Database junta todas las tablas en un solo archivo SQLite ("stockbodega.db")
// que vive dentro del celular. 100% offline. Cada tabla se maneja con su DAO.
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

    abstract fun usuarioDao(): UsuarioDao
    abstract fun bodegaDao(): BodegaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun productoDao(): ProductoDao
    abstract fun provClienteDao(): ProvClienteDao
    abstract fun almacenDao(): AlmacenDao
    abstract fun kardexDao(): KardexDao
    abstract fun ventaDao(): VentaDao
    abstract fun reportesDao(): ReportesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton: toda la app usa UNA sola instancia de la base de datos.
        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stockbodega.db"
                )
                    .addCallback(DatosIniciales)   // se ejecuta solo la primera vez
                    .build()
                    .also { INSTANCE = it }
            }

        // Lo usa la función "Restaurar copia de seguridad": cierra la BD para poder reemplazar el archivo
        fun cerrar() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

// EXPO: Datos mínimos para que la app sea usable desde el primer arranque:
// un usuario Administrador (usuario: admin / clave: admin123), la bodega y 6 categorías.
// Se ejecuta UNA sola vez, cuando Room crea la base de datos por primera vez.
private object DatosIniciales : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val hash = Seguridad.hash("admin123")
        db.execSQL(
            "INSERT INTO usuario (nombreCompleto, username, claveHash, rol, activo) " +
                    "VALUES ('Administrador', 'admin', '$hash', 'ADMINISTRADOR', 1)"
        )
        db.execSQL(
            "INSERT INTO bodega (nombreComercial, ruc, direccion, telefono) " +
                    "VALUES ('Mi Bodega', '', '', '')"
        )
        listOf("Abarrotes", "Lácteos", "Bebidas", "Conservas", "Limpieza", "Otros").forEach { nombre ->
            db.execSQL("INSERT INTO categoria (nombre, descripcion) VALUES ('$nombre', '')")
        }
    }
}