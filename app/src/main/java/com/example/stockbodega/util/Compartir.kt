package com.example.stockbodega.util

import android.content.Context
import android.content.Intent

// EXPO (RF-16): "Compartir" abre el menú NATIVO de Android (WhatsApp, correo, etc.).
// No necesitamos internet ni ninguna librería: solo un Intent ACTION_SEND con texto.
fun compartirTexto(context: Context, titulo: String, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, titulo)
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    val selector = Intent.createChooser(intent, titulo)
    selector.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(selector)
}