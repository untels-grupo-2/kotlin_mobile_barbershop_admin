package com.example.ta_avance.repository

import com.example.ta_avance.api.service.BarberoApiService
import com.example.ta_avance.dto.barbero.BarberoDto
import com.example.ta_avance.dto.barbero.BarberoSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarberoRepository @Inject constructor(
    private val barberoApiService: BarberoApiService
) {
    suspend fun listarBarberos(): Result<List<BarberoDto>> = runCatching {
        val response = barberoApiService.listarBarberos()
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun crearBarbero(dtoBarbero: RequestBody, imagen: MultipartBody.Part?): Result<List<BarberoDto>> = runCatching {
        val response = barberoApiService.crearBarbero(dtoBarbero, imagen)
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun eliminarBarbero(id: Int): Result<List<BarberoDto>> = runCatching {
        val response = barberoApiService.eliminarBarbero(id)
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun actualizarBarbero(id: Int, dtoBarbero: RequestBody, imagen: MultipartBody.Part?): Result<BarberoSimpleResponse> = runCatching {
        val response = barberoApiService.actualizarBarbero(id, dtoBarbero, imagen)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }
}