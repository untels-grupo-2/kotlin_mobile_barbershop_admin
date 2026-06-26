package com.example.ta_avance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shared.models.dto.servicio.ServicioDto

@Entity(tableName = "servicios")
data class ServicioEntity(
    @PrimaryKey val servicio_id: Int,
    val nombre: String?,
    val precio: Double,
    val descripcion: String?,
    val nombre_tipoServicio: String?,
    val tipoServicio_id: Int,
    val urlServicio: String?,
    val cacheTimestamp: Long = System.currentTimeMillis()
)

fun ServicioEntity.toDto() = ServicioDto(servicio_id, nombre, precio, descripcion, nombre_tipoServicio, tipoServicio_id, urlServicio)
fun ServicioDto.toEntity() = ServicioEntity(servicio_id, nombre, precio, descripcion, nombre_tipoServicio, tipoServicio_id, urlServicio)
