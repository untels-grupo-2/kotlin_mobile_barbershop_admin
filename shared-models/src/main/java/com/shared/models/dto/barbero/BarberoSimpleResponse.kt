package com.shared.models.dto.barbero

data class BarberoSimpleResponse(
    val status: Int = 0,
    val message: String? = null,
    val data: BarberoDto? = null
)
