package com.example.ta_avance.domain.usecase

import com.example.ta_avance.util.AppConstants
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerarUriWhatsAppUseCase @Inject constructor() {
    operator fun invoke(celular: String, mensaje: String): String {
        val mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8")
        return "https://wa.me/${AppConstants.PREFIJO_TELEFONO}$celular?text=$mensajeCodificado"
    }
}
