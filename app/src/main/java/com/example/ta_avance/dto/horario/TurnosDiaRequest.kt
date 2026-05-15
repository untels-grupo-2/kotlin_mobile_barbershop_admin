package com.example.ta_avance.dto.horario

data class TurnosDiaRequest(
    val dia: String,
    val turnosPorTipo: Map<Long, List<Long>>
)
