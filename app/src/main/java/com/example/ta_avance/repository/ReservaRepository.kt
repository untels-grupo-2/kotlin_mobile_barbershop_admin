package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.reserva.DtoReservaResponse
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservaRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun listarReservas(fecha: String, estado: String): Call<DtoReservaResponse> =
        authApiService.listarReservas(fecha, estado)

    fun listarReservasConId(fecha: String, estado: String, usuarioId: Long): Call<DtoReservaResponse> =
        authApiService.listarReservasConId(fecha, estado, usuarioId)

    fun cambiarEstadoReserva(reservaId: Long, estado: String, motivoDescripcion: String): Call<Void> =
        authApiService.cambiarEstadoReserva(reservaId, estado, motivoDescripcion)
}