package com.shared.models.dto.auth

data class RecuperacionRequest(
    val username: String,
    val mailTo: String
)
