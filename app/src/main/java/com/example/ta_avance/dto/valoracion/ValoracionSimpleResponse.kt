package com.example.ta_avance.dto.valoracion

data class ValoracionSimpleResponse(
    val status: Long = 0,
    val message: String? = null,
    val data: ValoracionDto? = null
)
