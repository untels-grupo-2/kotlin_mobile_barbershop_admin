package com.example.ta_avance.dto.barbero

data class BarberoResponse(
    val status: Int = 0,
    val message: String = "",
    val data: List<BarberoDto> = emptyList()
)