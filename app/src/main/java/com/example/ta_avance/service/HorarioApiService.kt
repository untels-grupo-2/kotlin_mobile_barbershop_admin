package com.example.ta_avance.api.service

import com.example.ta_avance.dto.horario.GenericResponse
import com.example.ta_avance.dto.horario.HorarioResponseWrapper
import com.example.ta_avance.dto.horario.TurnosDiaRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

interface HorarioApiService {
    @GET("api/horarioInstancia/actual")
    suspend fun obtenerHorarioActual(): Response<HorarioResponseWrapper>

    @PUT("api/horarioBarberoBase/actualizarTurnosDia")
    suspend fun actualizarTurnosDia(@Body request: TurnosDiaRequest): Response<GenericResponse>

    @PUT("api/horarioBarberoBase/confirmarHorario")
    suspend fun confirmarHorario(): Response<GenericResponse>

    @GET("api/reportes/horario")
    suspend fun exportarHorario(
        @Query("fechaInicio") fechaInicio: LocalDate,
        @Query("fechaFin") fechaFin: LocalDate
    ): Response<ResponseBody>
}