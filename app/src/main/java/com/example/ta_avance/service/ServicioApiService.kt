package com.example.ta_avance.api.service

import com.example.ta_avance.dto.servicio.ServicioResponse
import com.example.ta_avance.dto.servicio.ServicioSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ServicioApiService {
    @GET("api/servicio/listar")
    suspend fun listarServicios(): Response<ServicioResponse>

    @Multipart
    @POST("api/servicio/crear")
    suspend fun crearServicio(
        @Part("dtoServicio") dtoServicio: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ServicioResponse>

    @DELETE("api/servicio/eliminar/{id}")
    suspend fun eliminarServicio(@Path("id") id: Int): Response<ServicioResponse>

    @Multipart
    @PUT("api/servicio/actualizar/{id}")
    suspend fun actualizarServicio(
        @Path("id") id: Int,
        @Part("dtoServicio") dtoServicio: RequestBody,
        @Part imagen: MultipartBody.Part
    ): Response<ServicioSimpleResponse>
}