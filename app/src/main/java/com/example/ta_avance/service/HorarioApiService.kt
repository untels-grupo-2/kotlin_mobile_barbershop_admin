package com.example.ta_avance.api.service

import com.shared.models.dto.horario.GenericResponse
import com.shared.models.dto.horario.HorarioResponseWrapper
import com.shared.models.dto.horario.TurnosDiaRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

interface HorarioApiService {
    @GET("horarios-semana")
    suspend fun obtenerHorarioActual(): Response<HorarioResponseWrapper>

    @PUT("horarios-base")
    suspend fun actualizarTurnosDia(@Body request: TurnosDiaRequest): Response<GenericResponse>

    @PUT("horarios-base/confirmacion")
    suspend fun confirmarHorario(): Response<GenericResponse>

    @Streaming
    @GET("reporte/horarios")
    suspend fun exportarHorario(
        @Query("fechaInicio") fechaInicio: LocalDate,
        @Query("fechaFin") fechaFin: LocalDate
    ): Response<ResponseBody>
}
