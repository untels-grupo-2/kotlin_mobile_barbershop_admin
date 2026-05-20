package com.example.ta_avance.dto.horario

data class GenericResponse(
    val status: Int = 0,
    val message: String = "",
    val data: Any? = null
)