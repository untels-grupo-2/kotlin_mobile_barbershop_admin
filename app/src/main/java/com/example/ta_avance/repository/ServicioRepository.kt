package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ServicioApiService
import com.example.ta_avance.dto.servicio.ServicioDto
import com.example.ta_avance.dto.servicio.ServicioSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioRepository @Inject constructor(
    private val servicioApiService: ServicioApiService
) {
    suspend fun listarServicios(): Result<List<ServicioDto>> = runCatching {
        val response = servicioApiService.listarServicios()
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun crearServicio(dtoServicio: RequestBody, imagen: MultipartBody.Part): Result<List<ServicioDto>> = runCatching {
        val response = servicioApiService.crearServicio(dtoServicio, imagen)
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun eliminarServicio(id: Int): Result<List<ServicioDto>> = runCatching {
        val response = servicioApiService.eliminarServicio(id)
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun actualizarServicio(id: Int, dtoServicio: RequestBody, imagen: MultipartBody.Part): Result<ServicioSimpleResponse> = runCatching {
        val response = servicioApiService.actualizarServicio(id, dtoServicio, imagen)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }
}