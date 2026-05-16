package com.example.ta_avance.repository

import com.example.ta_avance.api.service.HorarioApiService
import com.example.ta_avance.dto.horario.GenericResponse
import com.example.ta_avance.dto.horario.HorarioResponseWrapper
import com.example.ta_avance.dto.horario.TurnosDiaRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HorarioRepository @Inject constructor(
    private val horarioApiService: HorarioApiService
) {
    suspend fun obtenerHorarioActual(): Result<HorarioResponseWrapper> = runCatching {
        val response = horarioApiService.obtenerHorarioActual()
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun actualizarTurnosDia(request: TurnosDiaRequest): Result<GenericResponse> = runCatching {
        val response = horarioApiService.actualizarTurnosDia(request)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun confirmarHorario(): Result<GenericResponse> = runCatching {
        val response = horarioApiService.confirmarHorario()
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun exportarHorario(fechaInicio: LocalDate, fechaFin: LocalDate): Result<ResponseBody> = runCatching {
        val response = horarioApiService.exportarHorario(fechaInicio, fechaFin)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }
}