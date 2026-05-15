package com.example.ta_avance.dto.barbero

data class BarberoDto(
    val barbero_id: Int = 0,
    val nombre: String = "",
    val estado: Int = 0,
    val urlBarbero: String? = null
)