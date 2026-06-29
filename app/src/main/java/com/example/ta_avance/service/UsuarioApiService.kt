package com.example.ta_avance.api.service

import com.shared.models.dto.horario.GenericResponse
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.login.LoginResponse
import com.example.ta_avance.dto.login.LoginResponseSimple
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApiService {
    @GET("usuarios")
    suspend fun listarUsuarios(): Response<LoginResponse>

    @GET("usuarios/{id}")
    suspend fun obtenerUsuarioPorId(@Path("id") id: Long): Response<LoginResponseSimple>

    @POST("autenticacion/registro/cliente")
    suspend fun registrarCliente(@Body registerRequest: LoginRequest): Response<GenericResponse>
}
