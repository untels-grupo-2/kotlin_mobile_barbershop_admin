package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ValoracionApiService
import com.example.ta_avance.dto.valoracion.ValoracionDto
import com.example.ta_avance.dto.valoracion.ValoracionSimpleResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValoracionRepository @Inject constructor(
    private val valoracionApiService: ValoracionApiService
) {
    suspend fun listarValoraciones(): Result<List<ValoracionDto>> = runCatching {
        val response = valoracionApiService.listarValoraciones()
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun responderValoracion(valoracionId: Long): Result<ValoracionSimpleResponse> = runCatching {
        val response = valoracionApiService.responderValoracion(valoracionId)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }
}