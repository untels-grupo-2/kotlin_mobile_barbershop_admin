package com.example.ta_avance.api

import com.example.ta_avance.util.PreferenciasHelper
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = preferenciasHelper.obtenerToken()
        val request = chain.request().newBuilder().apply {
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}