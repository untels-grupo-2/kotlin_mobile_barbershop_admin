package com.example.ta_avance.api.service

import com.example.ta_avance.dto.reserva.DtoReservaResponse
import retrofit2.Response
import retrofit2.http.*

interface ReservaApiService {
    @GET("api/reserva/admin/listar")
    suspend fun listarReservas(
        @Query("fecha") fecha: String,
        @Query("estado") estado: String
    ): Response<DtoReservaResponse>

    @GET("api/reserva/admin/listar")
    suspend fun listarReservasConId(
        @Query("fecha") fecha: String,
        @Query("estado") estado: String,
        @Query("usuarioId") usuarioId: Long
    ): Response<DtoReservaResponse>

    @PUT("api/reserva/admin/cambiar-estado/{reservaId}")
    suspend fun cambiarEstadoReserva(
        @Path("reservaId") reservaId: Long,
        @Query("estado") estado: String,
        @Query("motivoDescripcion") motivoDescripcion: String
    ): Response<Void>
}