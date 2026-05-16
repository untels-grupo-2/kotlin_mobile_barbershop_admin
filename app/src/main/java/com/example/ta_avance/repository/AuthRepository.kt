package com.example.ta_avance.repository

import com.example.ta_avance.api.service.AuthApiServiceKt
import com.example.ta_avance.dto.login.LoginDataSimpleResponse
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.recuperacion.RecuperacionRequest
import com.example.ta_avance.dto.refresh.RefreshRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiServiceKt
) {
    suspend fun login(loginRequest: LoginRequest): Result<LoginDataSimpleResponse> = runCatching {
        val response = authApiService.login(loginRequest)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun register(registerRequest: LoginRequest): Result<Unit> = runCatching {
        val response = authApiService.register(registerRequest)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun recuperarContraseña(recuperacionRequest: RecuperacionRequest): Result<Unit> = runCatching {
        val response = authApiService.recuperarContraseña(recuperacionRequest)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun refresh(refreshRequest: RefreshRequest): Result<LoginDataSimpleResponse> = runCatching {
        val response = authApiService.refresh(refreshRequest)
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}: ${response.message()}")
    }
}