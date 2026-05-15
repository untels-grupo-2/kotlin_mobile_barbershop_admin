package com.example.ta_avance.dto.login

data class LoginResponseSimple(
    val status: Int = 0,
    val message: String? = null,
    val data: LoginRequest? = null
)
