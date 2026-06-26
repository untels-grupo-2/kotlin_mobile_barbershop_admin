package com.example.ta_avance.repository

import com.example.ta_avance.api.service.UsuarioApiService
import com.example.ta_avance.data.local.dao.UsuarioDao
import com.example.ta_avance.data.local.entity.toDto
import com.example.ta_avance.data.local.entity.toEntity
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.util.CacheUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioApiService: UsuarioApiService,
    private val usuarioDao: UsuarioDao
) {
    suspend fun listarUsuarios(): Result<List<LoginRequest>> {
        val cached = usuarioDao.obtenerTodos()
        if (cached.isNotEmpty() && CacheUtils.esCacheValida(usuarioDao.obtenerTimestamp())) {
            return Result.success(cached.map { it.toDto() })
        }
        return runCatching {
            val response = usuarioApiService.listarUsuarios()
            if (response.isSuccessful) {
                val data = response.body()!!.data
                usuarioDao.eliminarTodos()
                usuarioDao.insertarTodos(data.map { it.toEntity() })
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    suspend fun obtenerUsuarioPorId(id: Long): Result<LoginRequest> {
        val cachedUser = usuarioDao.obtenerPorId(id)
        if (cachedUser != null && CacheUtils.esCacheValida(cachedUser.cacheTimestamp)) {
            return Result.success(cachedUser.toDto())
        }
        return runCatching {
            val response = usuarioApiService.obtenerUsuarioPorId(id)
            if (response.isSuccessful) {
                val data = response.body()!!.data!!
                usuarioDao.insertar(data.toEntity())
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cachedUser != null) cachedUser.toDto() else throw e
        }
    }
}
