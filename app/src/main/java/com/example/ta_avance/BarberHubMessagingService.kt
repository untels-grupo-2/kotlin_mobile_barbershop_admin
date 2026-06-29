package com.example.ta_avance

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ta_avance.actividades.AdminHomeActivity
import com.example.ta_avance.repository.NotificacionRepository
import com.example.ta_avance.util.PreferenciasHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BarberHubMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificacionRepository: NotificacionRepository
    @Inject lateinit var prefs: PreferenciasHelper

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo = message.notification?.title ?: "Nueva Reserva"
        val cuerpo = message.notification?.body ?: "Un cliente hizo una reserva"
        val reservaId = message.data["reservaId"]

        mostrarNotificacion(titulo, cuerpo, reservaId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Solo envía si hay sesión activa
        if (prefs.obtenerToken() == null) return
        CoroutineScope(Dispatchers.IO).launch {
            notificacionRepository.registrarFcmToken(token)
        }
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String, reservaId: String?) {
        val intent = Intent(this, AdminHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            reservaId?.let { putExtra("reservaId", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, BarberHubApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(reservaId.hashCode(), notification)
    }
}