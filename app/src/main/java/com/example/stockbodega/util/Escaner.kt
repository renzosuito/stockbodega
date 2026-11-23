package com.example.stockbodega.util

import android.content.Context
import android.widget.Toast
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

// EXPO (RF-18): escanear código de barras con la cámara del celular.
// Usamos el "Code Scanner" de Google Play Services: abre una pantalla de cámara lista,
// sin pedir permisos ni programar la cámara nosotros.
// Requiere un celular/emulador con Google Play Services (imagen "Google Play").
fun escanearCodigo(context: Context, onResultado: (String) -> Unit) {
    GmsBarcodeScanning.getClient(context)
        .startScan()
        .addOnSuccessListener { barcode ->
            barcode.rawValue?.let(onResultado)      // el texto del código leído
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "No se pudo escanear: ${e.message}", Toast.LENGTH_LONG).show()
        }
}