package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.domain.usecase.ConstruirMensajeConfirmacionReservaUseCase
import com.example.ta_avance.domain.usecase.GenerarUriWhatsAppUseCase
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.reserva.DtoReserva
import com.example.ta_avance.repository.ReservaRepository
import com.example.ta_avance.repository.UsuarioRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservasViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val construirMensajeConfirmacion: ConstruirMensajeConfirmacionReservaUseCase,
    private val generarUriWhatsApp: GenerarUriWhatsAppUseCase
) : ViewModel() {

    private val _reservasState = MutableStateFlow<UiState<List<DtoReserva>>>(UiState.Empty)
    val reservasState: StateFlow<UiState<List<DtoReserva>>> = _reservasState

    private val _cambioEstadoState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val cambioEstadoState: StateFlow<UiState<Boolean>> = _cambioEstadoState

    private val _usuarioState = MutableStateFlow<UiState<LoginRequest>>(UiState.Empty)
    val usuarioState: StateFlow<UiState<LoginRequest>> = _usuarioState

    fun cargarReservas(fecha: String, estado: String): Boolean {
        if ((estado == "CREADA" || estado == "CONFIRMADA") && fecha.isBlank()) {
            _reservasState.value = UiState.Error("FECHA_REQUERIDA")
            return false
        }
        _reservasState.value = UiState.Loading
        viewModelScope.launch {
            reservaRepository.listarReservas(fecha, estado)
                .onSuccess { _reservasState.value = UiState.Success(it) }
                .onFailure { _reservasState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
        return true
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

    fun generarTituloReservas(estado: String): String = "Reservas - $estado"

    fun calcularResumenReservas(reservas: List<DtoReserva>): Pair<String, String> {
        return if (reservas.isNotEmpty()) {
            "Reservas de ${reservas[0].usuarioNombre}" to "Monto Total: S/ ${reservas[0].montoTotal}"
        } else {
            "Reservas del cliente" to "Monto Total: S/ 0"
        }
    }

    fun generarUriWhatsAppConfirmacion(usuario: LoginRequest, reserva: DtoReserva): String {
        val mensaje = construirMensajeConfirmacion(
            usuario.nombre,
            reserva.fechaReserva,
            reserva.horarioRango,
            reserva.servicioNombre
        )
        return generarUriWhatsApp(usuario.celular, mensaje)
    }
}
