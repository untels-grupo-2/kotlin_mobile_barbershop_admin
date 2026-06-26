package com.example.ta_avance.repository

import android.content.Context
import android.os.Environment
import com.example.ta_avance.api.service.ReporteApiService
import com.example.ta_avance.dto.reporte.DtoReporte
import java.io.File
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReporteRepository @Inject constructor(
    private val reporteApiService: ReporteApiService
) {
    suspend fun obtenerReporte(fechaInicio: LocalDate, fechaFin: LocalDate, servicio: String): Result<DtoReporte> = runCatching {
        val response = reporteApiService.obtenerReporte(fechaInicio, fechaFin, servicio)
        if (response.isSuccessful) response.body()!!.data!!
        else error("Error ${response.code()}: ${response.message()}")
    }

    suspend fun descargarReportePdf(
        context: Context,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        servicio: String
    ): Result<File> = runCatching {
        val response = reporteApiService.descargarReportePdf(fechaInicio, fechaFin, servicio)
        if (!response.isSuccessful) error("Error ${response.code()}: ${response.message()}")
        val body = response.body()!!
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: error("Almacenamiento externo no disponible")
        val file = File(dir, "reporte_${fechaInicio}_${fechaFin}.pdf")
        body.byteStream().use { input -> file.outputStream().use { output -> output.write(input.readBytes()) } }
        file
    }
}