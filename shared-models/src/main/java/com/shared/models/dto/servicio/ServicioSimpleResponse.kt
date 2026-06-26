package com.shared.models.dto.servicio

data class ServicioSimpleResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: ServicioDto? = null
)
