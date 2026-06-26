package com.example.ta_avance.repository

import com.example.ta_avance.api.service.BarberoApiService
import com.example.ta_avance.data.local.dao.BarberoDao
import com.example.ta_avance.data.local.entity.toDto
import com.example.ta_avance.data.local.entity.toEntity
import com.shared.models.dto.barbero.BarberoDto
import com.shared.models.dto.barbero.BarberoSimpleResponse
import com.example.ta_avance.util.CacheUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarberoRepository @Inject constructor(
    private val barberoApiService: BarberoApiService,
    private val barberoDao: BarberoDao
) {
    suspend fun listarBarberos(): Result<List<BarberoDto>> {
        val cached = barberoDao.obtenerTodos()
        if (cached.isNotEmpty() && CacheUtils.esCacheValida(barberoDao.obtenerTimestamp())) {
            return Result.success(cached.map { it.toDto() })
        }
        return runCatching {
            val response = barberoApiService.listarBarberos()
            if (response.isSuccessful) {
                val data = response.body()!!.data
                barberoDao.eliminarTodos()
                barberoDao.insertarTodos(data.map { it.toEntity() })
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    suspend fun crearBarbero(dtoBarbero: RequestBody, imagen: MultipartBody.Part?): Result<List<BarberoDto>> = runCatching {
        val response = barberoApiService.crearBarbero(dtoBarbero, imagen)
        if (response.isSuccessful) {
            val data = response.body()!!.data
            barberoDao.eliminarTodos()
            barberoDao.insertarTodos(data.map { it.toEntity() })
            data
        } else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun eliminarBarbero(id: Int): Result<List<BarberoDto>> = runCatching {
        val response = barberoApiService.eliminarBarbero(id)
        if (response.isSuccessful) {
            val data = response.body()!!.data
            barberoDao.eliminarTodos()
            barberoDao.insertarTodos(data.map { it.toEntity() })
            data
        } else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun actualizarBarbero(id: Int, dtoBarbero: RequestBody, imagen: MultipartBody.Part?): Result<BarberoSimpleResponse> = runCatching {
        val response = barberoApiService.actualizarBarbero(id, dtoBarbero, imagen)
        if (response.isSuccessful) {
            barberoDao.eliminarTodos()
            response.body()!!
        } else error("Error ${response.code()}: ${response.message()}")
    }
}
