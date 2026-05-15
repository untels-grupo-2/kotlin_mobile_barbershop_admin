package com.example.ta_avance.dto.login

data class LoginDataSimpleResponse(
    val status: String = "",
    val message: String = "",
    val data: LoginData? = null
)