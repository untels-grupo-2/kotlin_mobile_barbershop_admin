package com.example.ta_avance.dto.reserva

data class DtoReserva(
    val reservaId: Long = 0,
    val barberoNombre: String? = null,
    val usuarioNombre: String? = null,
    val usuarioId: Long = 0,
    val horarioRango: String? = null,
    val estado: String? = null,
    val motivoDescripcion: String? = null,
    val adicionales: String? = null,
    val fechaCreacion: String? = null,
    val servicioNombre: String? = null,
    val precioServicio: Long = 0,
    val fechaReserva: String? = null,
    val urlPago: String? = null,
    val montoTotal: Long = 0
)
