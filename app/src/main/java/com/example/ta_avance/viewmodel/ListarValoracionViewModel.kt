package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.valoracion.ValoracionDto
import com.example.ta_avance.repository.ValoracionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarValoracionViewModel @Inject constructor(
    private val valoracionRepository: ValoracionRepository
) : ViewModel() {

    val valoraciones = MutableLiveData<List<ValoracionDto>>()
    val operacionExitosa = MutableLiveData<String>()
    val error = MutableLiveData<String>()

    fun obtenerValoraciones() {
        viewModelScope.launch {
            valoracionRepository.listarValoraciones()
                .onSuccess { valoraciones.postValue(it) }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun responderValoracion(valoracionId: Long) {
        viewModelScope.launch {
            valoracionRepository.responderValoracion(valoracionId)
                .onSuccess { operacionExitosa.postValue(it.message) }
                .onFailure { error.postValue(it.message) }
        }
    }
}