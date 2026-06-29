package com.example.ta_avance.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.repository.HorarioRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class HorarioActualViewModel @Inject constructor(
    private val horarioRepository: HorarioRepository
) : ViewModel() {

    private val _horariosState = MutableStateFlow<UiState<Map<String, Map<String, List<String>>>>>(UiState.Empty)
    val horariosState: StateFlow<UiState<Map<String, Map<String, List<String>>>>> = _horariosState

    private val _exportState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val exportState: StateFlow<UiState<Boolean>> = _exportState

    fun cargarHorarios() {
        _horariosState.value = UiState.Loading
        viewModelScope.launch {
            horarioRepository.obtenerHorarioActual()
                .onSuccess { wrapper ->
                    val semana = mutableMapOf<String, Map<String, List<String>>>()
                    for ((dia, turnosList) in wrapper.data) {
                        val turnos = mutableMapOf<String, MutableList<String>>()
                        for (turno in turnosList) {
                            turnos.getOrPut(turno.tipoHorario) { mutableListOf() }.add(turno.barbero)
                        }
                        semana[dia] = turnos
                    }
                    _horariosState.value = UiState.Success(semana)
                }
                .onFailure { _horariosState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun exportarHorario(context: Context) {
        val hoy = LocalDate.now()
        val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        _exportState.value = UiState.Loading
        viewModelScope.launch {
            horarioRepository.exportarHorario(lunes, domingo)
                .onSuccess { body ->
                    try {
                        val dir = context.getExternalFilesDir(null) ?: context.filesDir
                        val file = File(dir, "horario_barbero.pdf")
                        FileOutputStream(file).use { it.write(body.bytes()) }

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(
                                FileProvider.getUriForFile(context, "${context.packageName}.provider", file),
                                "application/pdf"
                            )
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        context.startActivity(intent)
                        _exportState.value = UiState.Success(true)
                    } catch (e: Exception) {
                        _exportState.value = UiState.Error("Error al guardar el PDF")
                    }
                }
                .onFailure { _exportState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
