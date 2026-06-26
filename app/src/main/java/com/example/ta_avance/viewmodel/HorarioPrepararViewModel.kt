package com.example.ta_avance.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shared.models.dto.barbero.BarberoDto
import com.example.ta_avance.repository.BarberoRepository
import com.example.ta_avance.repository.HorarioRepository
import com.shared.models.ui.state.UiState
import com.example.ta_avance.util.DiaSemana
import com.example.ta_avance.util.TurnoTipo
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
class HorarioPrepararViewModel @Inject constructor(
    private val barberoRepository: BarberoRepository,
    private val horarioRepository: HorarioRepository
) : ViewModel() {

    val dias = MutableStateFlow<List<String>>(DiaSemana.entries.map { it.name })
    val turnos = MutableStateFlow<List<String>>(TurnoTipo.entries.map { it.name })

    private val _barberosState = MutableStateFlow<UiState<List<BarberoDto>>>(UiState.Empty)
    val barberosState: StateFlow<UiState<List<BarberoDto>>> = _barberosState

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val operacionState: StateFlow<UiState<String>> = _operacionState

    fun cargarBarberos() {
        _barberosState.value = UiState.Loading
        viewModelScope.launch {
            barberoRepository.listarBarberos()
                .onSuccess { _barberosState.value = UiState.Success(it) }
                .onFailure { _barberosState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun guardarTurnosDia(dia: String, turnosPorTipo: Map<Long, List<Long>>) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            horarioRepository.actualizarTurnosDia(
                com.shared.models.dto.horario.TurnosDiaRequest(dia, turnosPorTipo)
            )
                .onSuccess { _operacionState.value = UiState.Success("${it.message} para el día ${dia.lowercase()}") }
                .onFailure { _operacionState.value = UiState.Error("Error al guardar turnos") }
        }
    }

    fun confirmarHorario() {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            horarioRepository.confirmarHorario()
                .onSuccess { _operacionState.value = UiState.Success(it.message) }
                .onFailure { _operacionState.value = UiState.Error("Error al confirmar horario") }
        }
    }

    fun exportarHorario(context: Context) {
        val hoy = LocalDate.now()
        val proximoLunes = hoy.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        val proximoDomingo = proximoLunes.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        viewModelScope.launch {
            horarioRepository.exportarHorario(proximoLunes, proximoDomingo)
                .onSuccess { body ->
                    try {
                        val file = File(context.getExternalFilesDir(null), "horario_barbero.pdf")
                        FileOutputStream(file).use { it.write(body.bytes()) }

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(
                                FileProvider.getUriForFile(context, "${context.packageName}.provider", file),
                                "application/pdf"
                            )
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        _operacionState.value = UiState.Error("Error al guardar el PDF")
                    }
                }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
