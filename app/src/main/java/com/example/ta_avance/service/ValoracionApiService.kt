package com.example.ta_avance.api.service

import com.example.ta_avance.dto.valoracion.ValoracionResponse
import com.example.ta_avance.dto.valoracion.ValoracionSimpleResponse
import retrofit2.Response
import retrofit2.http.*

interface ValoracionApiService {
    @GET("api/valoracion/listar")
    suspend fun listarValoraciones(): Response<ValoracionResponse>

    @GET("api/valoracion/responder/{valoracionId}")
    suspend fun responderValoracion(@Path("valoracionId") valoracionId: Long): Response<ValoracionSimpleResponse>
}