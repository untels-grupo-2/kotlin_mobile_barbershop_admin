package com.example.ta_avance.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.barbero.BarberoDto
import com.example.ta_avance.repository.BarberoRepository
import com.example.ta_avance.repository.HorarioRepository
import com.example.ta_avance.util.DiaSemana
import com.example.ta_avance.util.TurnoTipo
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val dias = MutableLiveData<List<String>>(
        DiaSemana.entries.map { it.name }
    )
    val turnos = MutableLiveData<List<String>>(
        TurnoTipo.entries.map { it.name }
    )
    val barberos = MutableLiveData<List<BarberoDto>>()
    val error = MutableLiveData<String>()
    val operacionExitosa = MutableLiveData<String>()

    fun cargarBarberos() {
        viewModelScope.launch {
            barberoRepository.listarBarberos()
                .onSuccess { barberos.postValue(it) }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun guardarTurnosDia(dia: String, turnosPorTipo: Map<Long, List<Long>>) {
        viewModelScope.launch {
            horarioRepository.actualizarTurnosDia(
                com.example.ta_avance.dto.horario.TurnosDiaRequest(dia, turnosPorTipo)
            )
                .onSuccess { operacionExitosa.postValue("${it.message} para el día ${dia.lowercase()}") }
                .onFailure { error.postValue("Error al guardar turnos") }
        }
    }

    fun confirmarHorario() {
        viewModelScope.launch {
            horarioRepository.confirmarHorario()
                .onSuccess { operacionExitosa.postValue(it.message) }
                .onFailure { error.postValue("Error al confirmar horario") }
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
                        error.postValue("Error al guardar el PDF")
                    }
                }
                .onFailure { error.postValue(it.message) }
        }
    }
}