package com.example.ta_avance.dto.login

data class LoginRequest @JvmOverloads constructor(
    val username: String = "",
    val password: String = "",
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val celular: String? = null,
    val usuario_id: Long = 0,
    val urlUsuario: String? = null
)