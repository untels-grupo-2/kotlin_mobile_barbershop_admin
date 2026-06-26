package com.example.ta_avance.domain.usecase

import com.example.ta_avance.util.AppConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConstruirMensajeBienvenidaUseCase @Inject constructor() {
    operator fun invoke(nombre: String, username: String): String {
        return """
            Hola *$nombre*, tu cuenta ha sido creada. Aquí tienes tus credenciales:

            👤 Usuario: *$username*
            🔑 Contraseña: *${AppConstants.PASSWORD_INICIAL}*

            📲 Descarga la app desde aquí: ${AppConstants.APP_URL}
            Por favor, cambia tu contraseña después de ingresar.
        """.trimIndent()
    }
}
