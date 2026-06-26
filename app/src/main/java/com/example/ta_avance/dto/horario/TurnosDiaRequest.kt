package com.shared.models.dto.horario

data class TurnosDiaRequest(
    val dia: String,
    val turnosPorTipo: Map<Long, List<Long>>
)
