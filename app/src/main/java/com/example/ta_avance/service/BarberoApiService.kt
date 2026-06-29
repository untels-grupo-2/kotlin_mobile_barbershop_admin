package com.example.ta_avance.api.service

import com.shared.models.dto.barbero.BarberoResponse
import com.shared.models.dto.barbero.BarberoSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BarberoApiService {
    @GET("barberos")
    suspend fun listarBarberos(): Response<BarberoResponse>

    @Multipart
    @POST("barberos")
    suspend fun crearBarbero(
        @Part("dtoBarbero") dtoBarbero: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<BarberoResponse>

    @DELETE("barberos/{id}")
    suspend fun eliminarBarbero(@Path("id") id: Int): Response<BarberoResponse>

    @Multipart
    @PUT("barberos/{id}")
    suspend fun actualizarBarbero(
        @Path("id") id: Int,
        @Part("dtoBarbero") dtoBarbero: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<BarberoSimpleResponse>
}
