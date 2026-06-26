package com.shared.models.dto.servicio

data class ServicioDto(
    val servicio_id: Int = 0,
    val nombre: String? = null,
    val precio: Double = 0.0,
    val descripcion: String? = null,
    val nombre_tipoServicio: String? = null,
    val tipoServicio_id: Int = 0,
    val urlServicio: String? = null
)
