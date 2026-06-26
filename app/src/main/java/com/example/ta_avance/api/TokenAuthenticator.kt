package com.example.ta_avance.api

import com.example.ta_avance.api.service.AuthApiServiceKt
import com.shared.models.dto.auth.RefreshRequest
import com.example.ta_avance.util.PreferenciasHelper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val prefs: PreferenciasHelper,
    private val authApi: AuthApiServiceKt
) : Authenticator {

    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Solo reintenta una vez, evita loop infinito
        if (response.request().header("X-Retry-After-Refresh") != null) {
            return null
        }

        val newToken = runBlocking {
            refreshMutex.withLock {
                // Si otro request ya refrescó, usar el token nuevo directamente
                val currentToken = prefs.obtenerToken()
                if (currentToken != response.request().header("Authorization")?.removePrefix("Bearer ")) {
                    return@withLock currentToken
                }
                // Hacer el refresh real
                try {
                    val refreshToken = prefs.obtenerRefreshToken() ?: return@withLock null
                    val refreshResponse = authApi.refresh(RefreshRequest(refreshToken))
                    if (refreshResponse.isSuccessful) {
                        val nuevoToken = refreshResponse.body()!!.data?.token ?: return@withLock null
                        val nuevoRefresh = refreshResponse.body()!!.data?.refreshToken ?: return@withLock null
                        prefs.guardarToken(nuevoToken)
                        prefs.guardarRefreshToken(nuevoRefresh)
                        nuevoToken
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        } ?: return null

        return response.request().newBuilder()
            .header("Authorization", "Bearer $newToken")
            .header("X-Retry-After-Refresh", "true")
            .build()
    }
}