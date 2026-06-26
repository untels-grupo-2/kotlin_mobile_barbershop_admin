package com.example.ta_avance.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.google.android.material.snackbar.Snackbar

object NetworkUtils {

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun mostrarBannerOffline(rootView: View): Snackbar =
        Snackbar.make(rootView, "Sin conexión — mostrando datos en caché", Snackbar.LENGTH_INDEFINITE)
            .also { it.show() }
}
