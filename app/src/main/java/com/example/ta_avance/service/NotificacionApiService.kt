package com.example.ta_avance.api.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificacionApiService {
    @POST("api/notificaciones/device-token")
    suspend fun registrarToken(@Body body: Map<String, String>): Response<Void>
}
