package com.example.ta_avance.dto.reporte

data class DtoReporteResponse(
    val status: Long = 0,
    val message: String = "",
    val data: DtoReporte? = null
)