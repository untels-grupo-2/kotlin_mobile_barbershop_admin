package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.barbero.BarberoResponse
import com.example.ta_avance.dto.barbero.BarberoSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarberoRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun listarBarberos(): Call<BarberoResponse> =
        authApiService.listarBarberos()

    fun crearBarbero(dtoBarbero: RequestBody, imagen: MultipartBody.Part): Call<BarberoResponse> =
        authApiService.crearBarbero(dtoBarbero, imagen)

    fun eliminarBarbero(id: Int): Call<BarberoResponse> =
        authApiService.eliminarBarbero(id)

    fun actualizarBarbero(id: Int, dtoBarbero: RequestBody, imagen: MultipartBody.Part): Call<BarberoSimpleResponse> =
        authApiService.actualizarBarbero(id, dtoBarbero, imagen)
}