package com.example.ta_avance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ta_avance.dto.reserva.DtoReserva

@Entity(tableName = "reservas")
data class ReservaEntity(
    @PrimaryKey val reservaId: Long,
    val cacheKey: String,
    val barberoNombre: String?,
    val usuarioNombre: String?,
    val usuarioId: Long,
    val horarioRango: String?,
    val estado: String?,
    val motivoDescripcion: String?,
    val adicionales: String?,
    val fechaCreacion: String?,
    val servicioNombre: String?,
    val precioServicio: Long,
    val fechaReserva: String?,
    val urlPago: String?,
    val montoTotal: Long,
    val cacheTimestamp: Long = System.currentTimeMillis()
)

fun ReservaEntity.toDto() = DtoReserva(
    reservaId, barberoNombre, usuarioNombre, usuarioId, horarioRango,
    estado, motivoDescripcion, adicionales, fechaCreacion,
    servicioNombre, precioServicio, fechaReserva, urlPago, montoTotal
)

fun DtoReserva.toEntity(cacheKey: String) = ReservaEntity(
    reservaId, cacheKey, barberoNombre, usuarioNombre, usuarioId, horarioRango,
    estado, motivoDescripcion, adicionales, fechaCreacion,
    servicioNombre, precioServicio, fechaReserva, urlPago, montoTotal
)
