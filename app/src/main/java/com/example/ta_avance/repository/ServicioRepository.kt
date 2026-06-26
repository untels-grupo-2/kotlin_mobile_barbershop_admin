package com.example.ta_avance.repository

import com.example.ta_avance.api.service.ServicioApiService
import com.example.ta_avance.data.local.dao.ServicioDao
import com.example.ta_avance.data.local.entity.toDto
import com.example.ta_avance.data.local.entity.toEntity
import com.shared.models.dto.servicio.ServicioDto
import com.shared.models.dto.servicio.ServicioSimpleResponse
import com.example.ta_avance.util.CacheUtils
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioRepository @Inject constructor(
    private val servicioApiService: ServicioApiService,
    private val servicioDao: ServicioDao
) {
    suspend fun listarServicios(): Result<List<ServicioDto>> {
        val cached = servicioDao.obtenerTodos()
        if (cached.isNotEmpty() && CacheUtils.esCacheValida(servicioDao.obtenerTimestamp())) {
            return Result.success(cached.map { it.toDto() })
        }
        return runCatching {
            val response = servicioApiService.listarServicios()
            if (response.isSuccessful) {
                val data = response.body()!!.data
                servicioDao.eliminarTodos()
                servicioDao.insertarTodos(data.map { it.toEntity() })
                data
            } else {
                error("Error ${response.code()}: ${response.message()}")
            }
        }.recoverCatching { e ->
            if (cached.isNotEmpty()) cached.map { it.toDto() } else throw e
        }
    }

    suspend fun crearServicio(dtoServicio: RequestBody, imagen: MultipartBody.Part?): Result<List<ServicioDto>> = runCatching {
        val response = servicioApiService.crearServicio(dtoServicio, imagen)
        if (response.isSuccessful) {
            val data = response.body()!!.data
            servicioDao.eliminarTodos()
            servicioDao.insertarTodos(data.map { it.toEntity() })
            data
        } else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun eliminarServicio(id: Int): Result<List<ServicioDto>> = runCatching {
        val response = servicioApiService.eliminarServicio(id)
        if (response.isSuccessful) {
            val data = response.body()!!.data
            servicioDao.eliminarTodos()
            servicioDao.insertarTodos(data.map { it.toEntity() })
            data
        } else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun actualizarServicio(id: Int, dtoServicio: RequestBody, imagen: MultipartBody.Part?): Result<ServicioSimpleResponse> = runCatching {
        val response = servicioApiService.actualizarServicio(id, dtoServicio, imagen)
        if (response.isSuccessful) {
            servicioDao.eliminarTodos()
            response.body()!!
        } else error("Error ${response.code()}: ${response.message()}")
    }
}
