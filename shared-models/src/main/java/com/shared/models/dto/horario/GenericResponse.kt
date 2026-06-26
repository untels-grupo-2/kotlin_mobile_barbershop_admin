package com.shared.models.dto.horario

data class GenericResponse(
    val status: Int = 0,
    val message: String = "",
    val data: Any? = null
)
