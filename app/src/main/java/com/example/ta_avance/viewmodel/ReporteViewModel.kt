package com.example.ta_avance.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.reporte.DtoReporte
import com.shared.models.dto.servicio.ServicioDto
import com.example.ta_avance.repository.ReporteRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val reporteRepository: ReporteRepository
) : ViewModel() {

    private val _reporteState = MutableStateFlow<UiState<DtoReporte>>(UiState.Empty)
    val reporteState: StateFlow<UiState<DtoReporte>> = _reporteState

    private val _descargaState = MutableStateFlow<UiState<File>>(UiState.Empty)
    val descargaState: StateFlow<UiState<File>> = _descargaState

    fun obtenerReporte(fechaInicio: LocalDate?, fechaFin: LocalDate?, servicio: String?) {
        if (fechaInicio == null || fechaFin == null) {
            _reporteState.value = UiState.Error("Selecciona ambas fechas")
            return
        }
        _reporteState.value = UiState.Loading
        viewModelScope.launch {
            reporteRepository.obtenerReporte(fechaInicio, fechaFin, servicio)
                .onSuccess { _reporteState.value = UiState.Success(it) }
                .onFailure { _reporteState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun descargarReporte(
        context: Context,
        reporte: DtoReporte,
        fechaInicio: LocalDate?,
        fechaFin: LocalDate?
    ) {
        _descargaState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    generarPdfLocal(context, reporte, fechaInicio, fechaFin)
                }
            }
                .onSuccess { _descargaState.value = UiState.Success(it) }
                .onFailure { _descargaState.value = UiState.Error(it.message ?: "Error al generar PDF") }
        }
    }

    private fun generarPdfLocal(
        context: Context,
        reporte: DtoReporte,
        fechaInicio: LocalDate?,
        fechaFin: LocalDate?
    ): File {
        val pageWidth = 595
        val pageHeight = 842

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
        }
        val paintLabel = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
            isFakeBoldText = true
        }
        val paintValue = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }
        val paintLine = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 80f

        canvas.drawText("Reporte de Reservas", 40f, y, paintTitle)
        y += 30f

        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, paintLine)
        y += 25f

        if (fechaInicio != null && fechaFin != null) {
            canvas.drawText("Periodo:", 40f, y, paintLabel)
            canvas.drawText("$fechaInicio  →  $fechaFin", 160f, y, paintValue)
            y += 30f
        }

        canvas.drawText("Servicio:", 40f, y, paintLabel)
        canvas.drawText(reporte.servicioNombre?.ifBlank { "Todos" } ?: "Todos", 160f, y, paintValue)
        y += 30f

        canvas.drawText("Cant. Reservas:", 40f, y, paintLabel)
        canvas.drawText("${reporte.cantidadReservas}", 160f, y, paintValue)
        y += 30f

        canvas.drawText("Monto Total:", 40f, y, paintLabel)
        canvas.drawText("S/ ${reporte.montoTotal}", 160f, y, paintValue)
        y += 20f

        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, paintLine)

        document.finishPage(page)

        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(dir, "reporte_reservas_${fechaInicio}_${fechaFin}.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }

    fun construirIntentCompartir(context: Context, file: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun generarNombresServicios(servicios: List<ServicioDto>): List<String> =
        listOf("Todos") + servicios.map { it.nombre ?: "" }

    fun resolverServicioSeleccionado(servicios: List<ServicioDto>, posicion: Int): String? =
        if (posicion == 0) null else servicios.getOrNull(posicion - 1)?.nombre
}
