package com.example.ta_avance

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BarberHubApp : Application() {

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Nuevas Reservas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de nuevas reservas de clientes"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(canal)
        }
    }

    companion object {
        const val CHANNEL_ID = "reservas_channel"
    }
}