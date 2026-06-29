package com.example.ta_avance.dto.reserva

data class DtoReservaResponse(
    val status: Long = 0,
    val message: String? = null,
    val data: DtoReservaPage? = null
)

data class DtoReservaPage(
    val content: List<DtoReserva> = emptyList()
)