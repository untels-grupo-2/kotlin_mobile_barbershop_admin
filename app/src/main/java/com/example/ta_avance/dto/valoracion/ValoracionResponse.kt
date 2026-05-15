package com.example.ta_avance.dto.valoracion

data class ValoracionResponse(
    val status: Long = 0,
    val message: String? = null,
    val data: List<ValoracionDto> = emptyList()
)
