package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ReservaApiService
import com.example.ta_avance.data.local.dao.ReservaDao
import com.example.ta_avance.data.local.entity.toDto
import com.example.ta_avance.data.local.entity.toEntity
import com.example.ta_avance.dto.reserva.DtoReserva
import com.example.ta_avance.util.CacheUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservaRepository @Inject constructor(
    private val reservaApiService: ReservaApiService,
    private val reservaDao: ReservaDao
) {
    suspend fun listarReservas(fecha: String, estado: String): Result<List<DtoReserva>> {
        val cacheKey = "$fecha|$estado"
        val cached = reservaDao.obtenerPorClave(cacheKey)
        if (cached.isNotEmpty() && CacheUtils.esCacheValida(reservaDao.obtenerTimestamp(cacheKey))) {
            return Result.success(cached.map { it.toDto() })
        }
        return runCatching {
            val response = reservaApiService.listarReservas(fecha, estado)
            if (response.isSuccessful) {
                val data = response.body()!!.data
                reservaDao.eliminarPorClave(cacheKey)
                reservaDao.insertarTodos(data.map { it.toEntity(cacheKey) })
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    suspend fun listarReservasConId(fecha: String, estado: String, usuarioId: Long): Result<List<DtoReserva>> {
        val cacheKey = "$fecha|$estado|$usuarioId"
        val cached = reservaDao.obtenerPorClave(cacheKey)
        if (cached.isNotEmpty() && CacheUtils.esCacheValida(reservaDao.obtenerTimestamp(cacheKey))) {
            return Result.success(cached.map { it.toDto() })
        }
        return runCatching {
            val response = reservaApiService.listarReservasConId(fecha, estado, usuarioId)
            if (response.isSuccessful) {
                val data = response.body()!!.data
                reservaDao.eliminarPorClave(cacheKey)
                reservaDao.insertarTodos(data.map { it.toEntity(cacheKey) })
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    suspend fun cambiarEstadoReserva(reservaId: Long, estado: String, motivoDescripcion: String): Result<Unit> = runCatching {
        val response = reservaApiService.cambiarEstadoReserva(reservaId, estado, motivoDescripcion)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.message()}")
    }

    fun observarReservas(fecha: String, estado: String): Flow<List<DtoReserva>> = flow {
        val cacheKey = "$fecha|$estado"
        val cached = reservaDao.obtenerPorClave(cacheKey)
        if (cached.isNotEmpty()) emit(cached.map { it.toDto() })
        while (true) {
            try {
                val response = reservaApiService.listarReservas(fecha, estado)
                if (response.isSuccessful) {
                    val data = response.body()!!.data
                    reservaDao.eliminarPorClave(cacheKey)
                    reservaDao.insertarTodos(data.map { it.toEntity(cacheKey) })
                    emit(data)
                }
            } catch (_: Exception) {}
            delay(30_000)
        }
    }
}
