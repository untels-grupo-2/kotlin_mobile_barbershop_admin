package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ReporteApiService
import com.example.ta_avance.dto.reporte.DtoReporte
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReporteRepository @Inject constructor(
    private val reporteApiService: ReporteApiService
) {
    suspend fun obtenerReporte(fechaInicio: LocalDate, fechaFin: LocalDate, servicio: String?): Result<DtoReporte> = runCatching {
        val response = reporteApiService.obtenerReporte(fechaInicio, fechaFin, servicio?.ifBlank { null })
        if (response.isSuccessful) response.body()!!.data!!
        else error("Error ${response.code()}: ${response.message()}")
    }

}