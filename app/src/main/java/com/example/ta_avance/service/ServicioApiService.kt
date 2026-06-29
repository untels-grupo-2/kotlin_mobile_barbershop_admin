package com.example.ta_avance.api.service

import com.shared.models.dto.servicio.ServicioResponse
import com.shared.models.dto.servicio.ServicioSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ServicioApiService {
    @GET("servicios")
    suspend fun listarServicios(): Response<ServicioResponse>

    @Multipart
    @POST("servicios")
    suspend fun crearServicio(
        @Part("dtoServicio") dtoServicio: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<ServicioResponse>

    @DELETE("servicios/{id}")
    suspend fun eliminarServicio(@Path("id") id: Int): Response<ServicioResponse>

    @Multipart
    @PUT("servicios/{id}")
    suspend fun actualizarServicio(
        @Path("id") id: Int,
        @Part("dtoServicio") dtoServicio: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<ServicioSimpleResponse>
}
