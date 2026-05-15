package com.example.ta_avance.dto.servicio

data class ServicioSimpleResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: ServicioDto? = null
)
