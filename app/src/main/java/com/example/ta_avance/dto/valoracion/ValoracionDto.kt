package com.example.ta_avance.dto.valoracion

data class ValoracionDto(
    val valoracion_id: Long = 0,
    val usuarioId: Long = 0,
    val celular: String? = null,
    val valoracion: Long = 0,
    val util: Boolean = false,
    val mensaje: String? = null,
    val usuario_nombre: String? = null
)
