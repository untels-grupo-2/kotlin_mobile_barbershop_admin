package com.example.ta_avance.api.service

import com.example.ta_avance.dto.reporte.DtoReporteResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

interface ReporteApiService {
    @GET("reservas/reportes")
    suspend fun obtenerReporte(
        @Query("fechaInicio") fechaInicio: LocalDate,
        @Query("fechaFin") fechaFin: LocalDate,
        @Query("servicio") servicio: String
    ): Response<DtoReporteResponse>

    @Streaming
    @GET("reporte/horarios")
    suspend fun descargarReportePdf(
        @Query("fechaInicio") fechaInicio: LocalDate,
        @Query("fechaFin") fechaFin: LocalDate
    ): Response<ResponseBody>
}
