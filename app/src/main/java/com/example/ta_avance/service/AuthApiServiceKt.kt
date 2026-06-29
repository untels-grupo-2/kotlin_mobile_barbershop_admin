package com.example.ta_avance.api.service

import com.shared.models.dto.horario.GenericResponse
import com.example.ta_avance.dto.login.LoginDataSimpleResponse
import com.example.ta_avance.dto.login.LoginRequest
import com.shared.models.dto.auth.RecuperacionRequest
import com.shared.models.dto.auth.RecuperacionResponse
import com.shared.models.dto.auth.RefreshRequest
import retrofit2.Response
import retrofit2.http.*

interface AuthApiServiceKt {
    @POST("autenticacion/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginDataSimpleResponse>

@POST("autenticacion/refresh-token")
    suspend fun refresh(@Body refreshRequest: RefreshRequest): Response<LoginDataSimpleResponse>

    @POST("email/password")
    suspend fun recuperarContraseña(@Body recuperacionRequest: RecuperacionRequest): Response<RecuperacionResponse>
}
