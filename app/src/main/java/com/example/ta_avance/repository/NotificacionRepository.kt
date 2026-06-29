package com.example.ta_avance.repository

import com.example.ta_avance.api.service.NotificacionApiService
import com.example.ta_avance.util.PreferenciasHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionRepository @Inject constructor(
    private val notificacionApiService: NotificacionApiService,
    private val prefs: PreferenciasHelper
) {
    suspend fun registrarFcmToken(fcmToken: String): Result<Unit> = runCatching {
        // Evita re-enviar si el token no cambió
        if (prefs.obtenerFcmToken() == fcmToken) return@runCatching
        val response = notificacionApiService.registrarToken(
            mapOf("token" to fcmToken, "plataforma" to "ANDROID")
        )
        if (!response.isSuccessful) error("Error ${response.code()} al registrar FCM token: ${response.errorBody()?.string()}")
        prefs.guardarFcmToken(fcmToken)
    }
}
