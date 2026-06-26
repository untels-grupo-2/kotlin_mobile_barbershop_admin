package com.example.ta_avance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ta_avance.dto.login.LoginRequest

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val usuario_id: Long,
    val username: String,
    val password: String,
    val nombre: String?,
    val apellido: String?,
    val email: String?,
    val celular: String?,
    val urlUsuario: String?,
    val cacheTimestamp: Long = System.currentTimeMillis()
)

fun UsuarioEntity.toDto() = LoginRequest(username, password, nombre, apellido, email, celular, usuario_id, urlUsuario)
fun LoginRequest.toEntity() = UsuarioEntity(usuario_id, username, password, nombre, apellido, email, celular, urlUsuario)
