package com.example.ta_avance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shared.models.dto.barbero.BarberoDto

@Entity(tableName = "barberos")
data class BarberoEntity(
    @PrimaryKey val barbero_id: Int,
    val nombre: String,
    val estado: Int,
    val urlBarbero: String?,
    val cacheTimestamp: Long = System.currentTimeMillis()
)

fun BarberoEntity.toDto() = BarberoDto(barbero_id, nombre, estado, urlBarbero)
fun BarberoDto.toEntity() = BarberoEntity(barbero_id, nombre, estado, urlBarbero)
