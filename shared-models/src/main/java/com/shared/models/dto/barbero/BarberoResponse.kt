package com.shared.models.dto.barbero

data class BarberoResponse(
    val status: Int = 0,
    val message: String = "",
    val data: List<BarberoDto> = emptyList()
)
