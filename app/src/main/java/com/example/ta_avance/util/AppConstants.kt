package com.example.ta_avance.util

object AppConstants {
    val TIPO_SERVICIO = linkedMapOf(
        "CORTES" to 1,
        "SKINCARE" to 2,
        "AFEITADO DE BARBA" to 3,
        "COLORACIÓN" to 4
    )

    val TURNO_IDS = mapOf(
        "MAÑANA" to 1L,
        "TARDE" to 2L,
        "NOCHE" to 3L
    )

    val ORDEN_DIAS = arrayOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
    val ORDEN_TURNOS = arrayOf("MAÑANA", "TARDE", "NOCHE")

    const val PREFIJO_TELEFONO = "51"
    const val APP_URL = "https://pagina-barbershop.vercel.app/"
    const val PASSWORD_INICIAL = "123456789"
}
