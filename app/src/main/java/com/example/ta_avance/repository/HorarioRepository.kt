package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.horario.GenericResponse
import com.example.ta_avance.dto.horario.HorarioResponseWrapper
import com.example.ta_avance.dto.horario.TurnosDiaRequest
import okhttp3.ResponseBody
import retrofit2.Call
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HorarioRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun obtenerHorarioActual(): Call<HorarioResponseWrapper> =
        authApiService.obtenerHorarioActual()

    fun actualizarTurnosDia(request: TurnosDiaRequest): Call<GenericResponse> =
        authApiService.actualizarTurnosDia(request)

    fun confirmarHorario(): Call<GenericResponse> =
        authApiService.confirmarHorario()

    fun exportarHorario(fechaInicio: LocalDate, fechaFin: LocalDate): Call<ResponseBody> =
        authApiService.exportarHorario(fechaInicio, fechaFin)
}