package com.example.ta_avance.dto.login

data class LoginResponse(
    val status: Int = 0,
    val message: String = "",
    val data: List<LoginRequest> = emptyList()
)