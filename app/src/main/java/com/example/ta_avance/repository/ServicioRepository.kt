package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.servicio.ServicioResponse
import com.example.ta_avance.dto.servicio.ServicioSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun listarServicios(): Call<ServicioResponse> =
        authApiService.listarServicios()

    fun crearServicio(dtoServicio: RequestBody, imagen: MultipartBody.Part): Call<ServicioResponse> =
        authApiService.crearServicio(dtoServicio, imagen)

    fun eliminarServicio(id: Int): Call<ServicioResponse> =
        authApiService.eliminarServicio(id)

    fun actualizarServicio(id: Int, dtoServicio: RequestBody, imagen: MultipartBody.Part): Call<ServicioSimpleResponse> =
        authApiService.actualizarServicio(id, dtoServicio, imagen)
}