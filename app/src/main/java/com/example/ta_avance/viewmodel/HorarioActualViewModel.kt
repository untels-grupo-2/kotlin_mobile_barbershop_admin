package com.example.ta_avance.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.repository.HorarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val horarios = MutableLiveData<Map<String, Map<String, List<String>>>>()
    val error = MutableLiveData<String>()
    val pdfListo = MutableLiveData<Boolean>()

    fun cargarHorarios() {
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
                    horarios.postValue(semana)
                }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun exportarHorario(context: Context) {
        val hoy = LocalDate.now()
        val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        viewModelScope.launch {
            horarioRepository.exportarHorario(lunes, domingo)
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
                        pdfListo.postValue(true)
                    } catch (e: Exception) {
                        error.postValue("Error al guardar el PDF")
                    }
                }
                .onFailure { error.postValue(it.message) }
        }
    }
}