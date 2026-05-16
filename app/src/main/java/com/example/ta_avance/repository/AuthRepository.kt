package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.horario.GenericResponse
import com.example.ta_avance.dto.login.LoginDataSimpleResponse
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.recuperacion.RecuperacionRequest
import com.example.ta_avance.dto.recuperacion.RecuperacionResponse
import com.example.ta_avance.dto.refresh.RefreshRequest
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun login(loginRequest: LoginRequest): Call<LoginDataSimpleResponse> =
        authApiService.login(loginRequest)

    fun register(registerRequest: LoginRequest): Call<GenericResponse> =
        authApiService.register(registerRequest)

    fun recuperarContraseña(recuperacionRequest: RecuperacionRequest): Call<RecuperacionResponse> =
        authApiService.recuperarContraseña(recuperacionRequest)

    fun refresh(refreshRequest: RefreshRequest): Call<LoginDataSimpleResponse> =
        authApiService.refresh(refreshRequest)
}