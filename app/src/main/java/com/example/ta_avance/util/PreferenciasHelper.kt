package com.example.ta_avance.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenciasHelper @Inject constructor(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun guardarToken(token: String) = preferences.edit().putString(TOKEN_KEY, token).apply()

    fun guardarRefreshToken(refreshToken: String) = preferences.edit().putString(REFRESH, refreshToken).apply()

    fun obtenerRefreshToken(): String? = preferences.getString(REFRESH, null)

    fun obtenerToken(): String? = preferences.getString(TOKEN_KEY, null)

    fun limpiarPreferencias() = preferences.edit().clear().apply()

    companion object {
        private const val PREFS_NAME = "MisPreferencias"
        private const val TOKEN_KEY = "token"
        private const val REFRESH = "refreshToken"
    }
}