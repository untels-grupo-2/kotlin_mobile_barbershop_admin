package com.example.ta_avance.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConstruirMensajeConfirmacionReservaUseCase @Inject constructor() {
    operator fun invoke(
        nombreUsuario: String,
        fechaReserva: String,
        horarioRango: String,
        servicioNombre: String
    ): String {
        return """
            Hola *$nombreUsuario*, tu reserva ha sido confirmada 🎉

            📅 Fecha: *$fechaReserva*
            🕒 Horario: *$horarioRango*
            💇 Servicio: *$servicioNombre*

            Por favor, acude puntual a tu cita. ¡Gracias por elegirnos! 💈
        """.trimIndent()
    }
}
