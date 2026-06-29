package com.example.ta_avance.repository

import com.example.ta_avance.api.service.AuthApiServiceKt
import com.example.ta_avance.api.service.UsuarioApiService
import com.example.ta_avance.dto.login.LoginDataSimpleResponse
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.util.errorMessage
import com.shared.models.dto.auth.RecuperacionRequest
import com.shared.models.dto.auth.RefreshRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiServiceKt,
    private val usuarioApiService: UsuarioApiService,
    private val usuarioRepository: UsuarioRepository
) {
    suspend fun login(loginRequest: LoginRequest): Result<LoginDataSimpleResponse> = runCatching {
        val response = authApiService.login(loginRequest)
        if (response.isSuccessful) response.body()!!
        else error(response.errorMessage())
    }

    suspend fun register(registerRequest: LoginRequest): Result<Unit> = runCatching {
        val response = usuarioApiService.registrarCliente(registerRequest)
        if (!response.isSuccessful) error(response.errorMessage())
        usuarioRepository.invalidarCache()
    }

    suspend fun recuperarContraseña(recuperacionRequest: RecuperacionRequest): Result<Unit> = runCatching {
        val response = authApiService.recuperarContraseña(recuperacionRequest)
        if (!response.isSuccessful) error(response.errorMessage())
    }

    suspend fun refresh(refreshRequest: RefreshRequest): Result<LoginDataSimpleResponse> = runCatching {
        val response = authApiService.refresh(refreshRequest)
        if (response.isSuccessful) response.body()!!
        else error(response.errorMessage())
    }
}