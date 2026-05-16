package com.example.ta_avance.repository

import com.example.ta_avance.api.service.UsuarioApiService
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.login.LoginResponseSimple
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioApiService: UsuarioApiService
) {
    suspend fun listarUsuarios(): Result<List<LoginRequest>> = runCatching {
        val response = usuarioApiService.listarUsuarios()
        if (response.isSuccessful) response.body()!!.data
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun obtenerUsuarioPorId(id: Long): Result<LoginRequest> = runCatching {
        val response = usuarioApiService.obtenerUsuarioPorId(id)
        if (response.isSuccessful) response.body()!!.data!!
        else error("Error ${response.code()}: ${response.message()}")
    }
}