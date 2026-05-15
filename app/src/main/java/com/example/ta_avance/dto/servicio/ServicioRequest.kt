package com.example.ta_avance.dto.servicio

data class ServicioRequest(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val tipoServicio_id: Int
)
