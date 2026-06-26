package com.shared.models.dto.servicio

data class ServicioResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: List<ServicioDto> = emptyList()
)
