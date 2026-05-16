package com.example.ta_avance.repository

import com.example.ta_avance.api.AuthApiService
import com.example.ta_avance.dto.login.LoginResponse
import com.example.ta_avance.dto.login.LoginResponseSimple
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    fun listarUsuarios(): Call<LoginResponse> =
        authApiService.listarUsuarios()

    fun obtenerUsuarioPorId(id: Long): Call<LoginResponseSimple> =
        authApiService.obtenerUsuarioPorId(id)
}