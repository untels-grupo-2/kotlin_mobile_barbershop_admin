package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ValoracionApiService
import com.example.ta_avance.data.local.dao.ValoracionDao
import com.example.ta_avance.data.local.entity.toDto
import com.example.ta_avance.data.local.entity.toEntity
import com.example.ta_avance.dto.valoracion.ValoracionDto
import com.example.ta_avance.dto.valoracion.ValoracionSimpleResponse
import com.example.ta_avance.util.CacheUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValoracionRepository @Inject constructor(
    private val valoracionApiService: ValoracionApiService,
    private val valoracionDao: ValoracionDao
) {
    suspend fun listarValoraciones(): Result<List<ValoracionDto>> {
        val cached = valoracionDao.obtenerTodos()
        if (cached.isNotEmpty() && CacheUtils.esCacheValida(valoracionDao.obtenerTimestamp())) {
            return Result.success(cached.map { it.toDto() })
        }
        return runCatching {
            val response = valoracionApiService.listarValoraciones()
            if (response.isSuccessful) {
                val data = response.body()!!.data
                valoracionDao.eliminarTodos()
                valoracionDao.insertarTodos(data.map { it.toEntity() })
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    suspend fun responderValoracion(valoracionId: Long): Result<ValoracionSimpleResponse> = runCatching {
        val response = valoracionApiService.responderValoracion(valoracionId)
        if (response.isSuccessful) {
            valoracionDao.eliminarTodos()
            response.body()!!
        } else error("Error ${response.code()}: ${response.message()}")
    }
}
