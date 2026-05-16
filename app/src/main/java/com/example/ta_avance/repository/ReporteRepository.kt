package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.reporte.DtoReporteResponse
import retrofit2.Call
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReporteRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun obtenerReporte(fechaInicio: LocalDate, fechaFin: LocalDate, servicio: String): Call<DtoReporteResponse> =
        authApiService.obtenerReporte(fechaInicio, fechaFin, servicio)
}