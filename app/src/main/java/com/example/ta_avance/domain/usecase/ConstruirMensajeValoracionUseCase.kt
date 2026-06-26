package com.example.ta_avance.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConstruirMensajeValoracionUseCase @Inject constructor() {
    operator fun invoke(nombreUsuario: String): String {
        return """
            Hola *$nombreUsuario*, muchas gracias por tu opinión.
            Nos ayuda a mejorar nuestro servicio.
        """.trimIndent()
    }
}
