package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.domain.usecase.ConstruirMensajeValoracionUseCase
import com.example.ta_avance.domain.usecase.GenerarUriWhatsAppUseCase
import com.example.ta_avance.dto.valoracion.ValoracionDto
import com.example.ta_avance.repository.ValoracionRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarValoracionViewModel @Inject constructor(
    private val valoracionRepository: ValoracionRepository,
    private val construirMensajeValoracion: ConstruirMensajeValoracionUseCase,
    private val generarUriWhatsApp: GenerarUriWhatsAppUseCase
) : ViewModel() {

    private val _valoracionesState = MutableStateFlow<UiState<List<ValoracionDto>>>(UiState.Empty)
    val valoracionesState: StateFlow<UiState<List<ValoracionDto>>> = _valoracionesState

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val operacionState: StateFlow<UiState<String>> = _operacionState

    fun obtenerValoraciones() {
        _valoracionesState.value = UiState.Loading
        viewModelScope.launch {
            valoracionRepository.listarValoraciones()
                .onSuccess { _valoracionesState.value = UiState.Success(it) }
                .onFailure { _valoracionesState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun responderValoracion(valoracionId: Long) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            valoracionRepository.responderValoracion(valoracionId)
                .onSuccess { _operacionState.value = UiState.Success(it.message) }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun generarUriWhatsAppValoracion(valoracion: ValoracionDto): String {
        val mensaje = construirMensajeValoracion(valoracion.usuario_nombre)
        return generarUriWhatsApp(valoracion.celular, mensaje)
    }
}
