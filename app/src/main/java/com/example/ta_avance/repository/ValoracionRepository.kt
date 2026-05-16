package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.valoracion.ValoracionResponse
import com.example.ta_avance.dto.valoracion.ValoracionSimpleResponse
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValoracionRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun listarValoraciones(): Call<ValoracionResponse> =
        authApiService.listarValoraciones()

    fun responderValoracion(valoracionId: Long): Call<ValoracionSimpleResponse> =
        authApiService.responderValoracion(valoracionId)
}