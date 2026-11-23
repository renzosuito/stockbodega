package com.example.stockbodega.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// EXPO (RF-17): la base de datos SQLite es UN ARCHIVO ("stockbodega.db"). Respaldar es
// copiar ese archivo a otra carpeta; restaurar es copiarlo de vuelta. Sin internet.
object RespaldoHelper {

    private const val NOMBRE_BD = "stockbodega.db"

    private fun carpetaRespaldos(context: Context): File {
        val base = context.getExternalFilesDir(null) ?: context.filesDir
        return File(base, "respaldos").apply { mkdirs() }
    }

    /** Copia la base de datos a /respaldos con fecha y hora. Devuelve el archivo creado. */
    fun respaldar(context: Context): File {
        val db = AppDatabase.getDatabase(context)
        // SQLite usa un archivo temporal (WAL). Esto obliga a volcar todo al archivo principal.
        db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use { it.moveToFirst() }
        val origen = context.getDatabasePath(NOMBRE_BD)
        val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val destino = File(carpetaRespaldos(context), "stockbodega_$marca.db")
        origen.copyTo(destino, overwrite = true)
        return destino
    }

    /** Restaura el respaldo más reciente. Devuelve false si no hay ninguno. */
    fun restaurar(context: Context): Boolean {
        val ultimo = carpetaRespaldos(context)
            .listFiles { archivo -> archivo.extension == "db" }
            ?.maxByOrNull { it.lastModified() }
            ?: return false

        AppDatabase.cerrar()   // hay que cerrar la BD antes de reemplazar su archivo
        val destino = context.getDatabasePath(NOMBRE_BD)
        File(destino.path + "-wal").delete()
        File(destino.path + "-shm").delete()
        ultimo.copyTo(destino, overwrite = true)
        return true
    }
}
