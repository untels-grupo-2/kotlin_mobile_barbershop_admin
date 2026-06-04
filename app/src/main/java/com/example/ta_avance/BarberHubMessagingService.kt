package com.example.ta_avance

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ta_avance.actividades.AdminHomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BarberHubMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo = message.notification?.title ?: "Nueva Reserva"
        val cuerpo = message.notification?.body ?: "Un cliente hizo una reserva"
        val reservaId = message.data["reservaId"]

        mostrarNotificacion(titulo, cuerpo, reservaId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Aquí se enviará el token al backend cuando esté listo
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