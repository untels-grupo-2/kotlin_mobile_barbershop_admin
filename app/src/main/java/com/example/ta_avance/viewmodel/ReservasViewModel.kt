package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.reserva.DtoReserva
import com.example.ta_avance.repository.ReservaRepository
import com.example.ta_avance.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservasViewModel @Inject constructor(
    private val reservaRepository: ReservaRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    val reservas = MutableLiveData<List<DtoReserva>>()
    val mensajeError = MutableLiveData<String>()
    val cambioEstadoExitoso = MutableLiveData<Boolean>()
    val usuarioPorIdLiveData = MutableLiveData<LoginRequest>()

    fun cargarReservas(fecha: String, estado: String) {
        viewModelScope.launch {
            reservaRepository.listarReservas(fecha, estado)
                .onSuccess { reservas.postValue(it) }
                .onFailure { mensajeError.postValue(it.message) }
        }
    }

    fun cargarReservasConId(fecha: String, estado: String, usuarioId: Long) {
        viewModelScope.launch {
            reservaRepository.listarReservasConId(fecha, estado, usuarioId)
                .onSuccess { reservas.postValue(it) }
                .onFailure { mensajeError.postValue(it.message) }
        }
    }

    fun cambiarEstadoReserva(reservaId: Long, nuevoEstado: String, motivoDescripcion: String) {
        viewModelScope.launch {
            reservaRepository.cambiarEstadoReserva(reservaId, nuevoEstado, motivoDescripcion)
                .onSuccess { cambioEstadoExitoso.postValue(true) }
                .onFailure {
                    mensajeError.postValue(it.message)
                    cambioEstadoExitoso.postValue(false)
                }
        }
    }

    fun obtenerUsuarioPorId(usuarioId: Long) {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioPorId(usuarioId)
                .onSuccess { usuarioPorIdLiveData.postValue(it) }
                .onFailure { mensajeError.postValue(it.message) }
        }
    }
}