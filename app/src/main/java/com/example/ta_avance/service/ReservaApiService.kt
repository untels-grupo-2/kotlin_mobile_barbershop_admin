package com.example.ta_avance.api.service

import com.example.ta_avance.dto.reserva.DtoReservaResponse
import retrofit2.Response
import retrofit2.http.*

interface ReservaApiService {
    @GET("reservas/admin")
    suspend fun listarReservas(
        @Query("fechaDesde") fecha: String,
        @Query("estado") estado: String
    ): Response<DtoReservaResponse>

    @GET("reservas/admin")
    suspend fun listarReservasConId(
        @Query("fechaDesde") fecha: String,
        @Query("estado") estado: String,
        @Query("clienteId") usuarioId: Long
    ): Response<DtoReservaResponse>

    @PUT("reservas/{id}/estado")
    suspend fun cambiarEstadoReserva(
        @Path("id") reservaId: Long,
        @Query("estado") estado: String,
        @Query("motivoDescripcion") motivoDescripcion: String
    ): Response<Void>
}
