package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.reporte.DtoReporte
import com.example.ta_avance.repository.ReporteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReporteViewModel @Inject constructor(
    private val reporteRepository: ReporteRepository
) : ViewModel() {

    val reporte = MutableLiveData<DtoReporte>()
    val error = MutableLiveData<String>()

    fun obtenerReporte(fechaInicio: LocalDate, fechaFin: LocalDate, servicio: String?) {
        viewModelScope.launch {
            reporteRepository.obtenerReporte(fechaInicio, fechaFin, servicio ?: "")
                .onSuccess { reporte.postValue(it) }
                .onFailure { error.postValue(it.message) }
        }
    }
}