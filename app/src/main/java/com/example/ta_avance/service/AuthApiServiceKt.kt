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
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginDataSimpleResponse>

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: LoginRequest): Response<GenericResponse>

    @POST("api/auth/refreshToken")
    suspend fun refresh(@Body refreshRequest: RefreshRequest): Response<LoginDataSimpleResponse>

    @POST("emailPassword/sendEmail")
    suspend fun recuperarContraseña(@Body recuperacionRequest: RecuperacionRequest): Response<RecuperacionResponse>
}