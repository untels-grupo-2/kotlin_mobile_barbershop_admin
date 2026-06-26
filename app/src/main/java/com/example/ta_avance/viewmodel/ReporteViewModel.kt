package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.reporte.DtoReporte
import com.example.ta_avance.dto.servicio.ServicioDto
import com.example.ta_avance.repository.ReporteRepository
import com.example.ta_avance.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val reporteRepository: ReporteRepository
) : ViewModel() {

    private val _reporteState = MutableStateFlow<UiState<DtoReporte>>(UiState.Empty)
    val reporteState: StateFlow<UiState<DtoReporte>> = _reporteState

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

    fun generarNombresServicios(servicios: List<ServicioDto>): List<String> =
        listOf("Todos") + servicios.map { it.nombre }

    fun resolverServicioSeleccionado(servicios: List<ServicioDto>, posicion: Int): String? =
        if (posicion == 0) null else servicios.getOrNull(posicion - 1)?.nombre
}
