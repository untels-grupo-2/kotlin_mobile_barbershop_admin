package com.example.ta_avance.api.service

import com.example.ta_avance.dto.barbero.BarberoResponse
import com.example.ta_avance.dto.barbero.BarberoSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BarberoApiService {
    @GET("api/barbero/listar")
    suspend fun listarBarberos(): Response<BarberoResponse>

    @Multipart
    @POST("api/barbero/crear")
    suspend fun crearBarbero(
        @Part("dtoBarbero") dtoBarbero: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<BarberoResponse>

    @DELETE("api/barbero/eliminar/{id}")
    suspend fun eliminarBarbero(@Path("id") id: Int): Response<BarberoResponse>

    @Multipart
    @PUT("api/barbero/actualizar/{id}")
    suspend fun actualizarBarbero(
        @Path("id") id: Int,
        @Part("dtoBarbero") dtoBarbero: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<BarberoSimpleResponse>
}