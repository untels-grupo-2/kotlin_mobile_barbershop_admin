package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.reserva.DtoReserva
import com.example.ta_avance.repository.ReservaRepository
import com.example.ta_avance.repository.UsuarioRepository
import com.example.ta_avance.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservasViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _reservasState = MutableStateFlow<UiState<List<DtoReserva>>>(UiState.Empty)
    val reservasState: StateFlow<UiState<List<DtoReserva>>> = _reservasState

    private val _cambioEstadoState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val cambioEstadoState: StateFlow<UiState<Boolean>> = _cambioEstadoState

    private val _usuarioState = MutableStateFlow<UiState<LoginRequest>>(UiState.Empty)
    val usuarioState: StateFlow<UiState<LoginRequest>> = _usuarioState

    fun cargarReservas(fecha: String, estado: String) {
        _reservasState.value = UiState.Loading
        viewModelScope.launch {
            reservaRepository.listarReservas(fecha, estado)
                .onSuccess { _reservasState.value = UiState.Success(it) }
                .onFailure { _reservasState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun cargarReservasConId(fecha: String, estado: String, usuarioId: Long) {
        _reservasState.value = UiState.Loading
        viewModelScope.launch {
            reservaRepository.listarReservasConId(fecha, estado, usuarioId)
                .onSuccess { _reservasState.value = UiState.Success(it) }
                .onFailure { _reservasState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun cambiarEstadoReserva(reservaId: Long, nuevoEstado: String, motivoDescripcion: String) {
        _cambioEstadoState.value = UiState.Loading
        viewModelScope.launch {
            reservaRepository.cambiarEstadoReserva(reservaId, nuevoEstado, motivoDescripcion)
                .onSuccess { _cambioEstadoState.value = UiState.Success(true) }
                .onFailure { _cambioEstadoState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun obtenerUsuarioPorId(usuarioId: Long) {
        _usuarioState.value = UiState.Loading
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioPorId(usuarioId)
                .onSuccess { _usuarioState.value = UiState.Success(it) }
                .onFailure { _usuarioState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
