package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ReservaApiService
import com.example.ta_avance.dto.reserva.DtoReserva
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservaRepository @Inject constructor(
    private val reservaApiService: ReservaApiService
) {
    suspend fun listarReservas(fecha: String, estado: String): Result<List<DtoReserva>> = runCatching {
        val response = reservaApiService.listarReservas(fecha, estado)
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun listarReservasConId(fecha: String, estado: String, usuarioId: Long): Result<List<DtoReserva>> = runCatching {
        val response = reservaApiService.listarReservasConId(fecha, estado, usuarioId)
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun cambiarEstadoReserva(reservaId: Long, estado: String, motivoDescripcion: String): Result<Unit> = runCatching {
        val response = reservaApiService.cambiarEstadoReserva(reservaId, estado, motivoDescripcion)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.message()}")
    }

    fun observarReservas(fecha: String, estado: String): Flow<List<DtoReserva>> = flow {
        while (true) {
            val response = reservaApiService.listarReservas(fecha, estado)
            if (response.isSuccessful) emit(response.body()!!.data)
            delay(30_000)
        }
    }
}