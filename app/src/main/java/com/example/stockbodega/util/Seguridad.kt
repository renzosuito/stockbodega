package com.example.stockbodega.util

import java.security.MessageDigest

// EXPO (RNF-09): las claves NUNCA se guardan en texto plano. Se guarda su "hash"
// SHA-256: una huella de la clave que no se puede convertir de vuelta a la clave.
// Para entrar, se calcula el hash de lo que escribió el usuario y se compara.
// (En una app comercial se usaría PBKDF2 o bcrypt con sal distinta por usuario.)
object Seguridad {
    private const val SAL = "StockBodega-2026"   // texto fijo que se mezcla con la clave

    fun hash(clave: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((SAL + clave).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }   // bytes -> texto hexadecimal
    }

    fun verificar(clave: String, hashGuardado: String): Boolean = hash(clave) == hashGuardado
}