package com.example.ta_avance.api.service

import com.example.ta_avance.dto.valoracion.ValoracionResponse
import com.example.ta_avance.dto.valoracion.ValoracionSimpleResponse
import retrofit2.Response
import retrofit2.http.*

interface ValoracionApiService {
    @GET("valoraciones")
    suspend fun listarValoraciones(): Response<ValoracionResponse>

    @PATCH("valoraciones/{id}/estado")
    suspend fun responderValoracion(@Path("id") valoracionId: Long): Response<ValoracionSimpleResponse>
}
