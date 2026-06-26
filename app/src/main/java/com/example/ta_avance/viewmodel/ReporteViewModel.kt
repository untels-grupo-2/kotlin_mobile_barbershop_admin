package com.example.ta_avance.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.reporte.DtoReporte
import com.shared.models.dto.servicio.ServicioDto
import com.example.ta_avance.repository.ReporteRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
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
            reporteRepository.obtenerReporte(fechaInicio, fechaFin, servicio ?: "")
                .onSuccess { _reporteState.value = UiState.Success(it) }
                .onFailure { _reporteState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun descargarReporte(context: Context, fechaInicio: LocalDate?, fechaFin: LocalDate?) {
        if (fechaInicio == null || fechaFin == null) {
            _descargaState.value = UiState.Error("Selecciona ambas fechas para descargar")
            return
        }
        _descargaState.value = UiState.Loading
        viewModelScope.launch {
            reporteRepository.descargarReportePdf(context, fechaInicio, fechaFin)
                .onSuccess { _descargaState.value = UiState.Success(it) }
                .onFailure { _descargaState.value = UiState.Error(it.message ?: "Error al descargar") }
        }
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
        listOf("Todos") + servicios.map { it.nombre }

    fun resolverServicioSeleccionado(servicios: List<ServicioDto>, posicion: Int): String? =
        if (posicion == 0) null else servicios.getOrNull(posicion - 1)?.nombre
}
