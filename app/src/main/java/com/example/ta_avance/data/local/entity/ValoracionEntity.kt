package com.example.ta_avance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ta_avance.dto.valoracion.ValoracionDto

@Entity(tableName = "valoraciones")
data class ValoracionEntity(
    @PrimaryKey val valoracion_id: Long,
    val usuarioId: Long,
    val celular: String?,
    val valoracion: Long,
    val util: Boolean,
    val mensaje: String?,
    val usuario_nombre: String?,
    val estado: Int = 1,
    val cacheTimestamp: Long = System.currentTimeMillis()
)

fun ValoracionEntity.toDto() = ValoracionDto(valoracion_id, usuarioId, celular, valoracion, util, mensaje, usuario_nombre, estado)
fun ValoracionDto.toEntity() = ValoracionEntity(valoracion_id, usuarioId, celular, valoracion, util, mensaje, usuario_nombre, estado)
