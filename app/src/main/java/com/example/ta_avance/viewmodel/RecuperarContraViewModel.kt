package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shared.models.dto.auth.RecuperacionRequest
import com.example.ta_avance.repository.AuthRepository
import com.shared.models.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecuperarContraViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _recuperarState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val recuperarState: StateFlow<UiState<String>> = _recuperarState

    fun recuperar(usuario: String, correo: String) {
        if (usuario.isBlank() || correo.isBlank()) {
            _recuperarState.value = UiState.Error("Completa todos los campos")
            return
        }
        _recuperarState.value = UiState.Loading
        viewModelScope.launch {
            authRepository.recuperarContraseña(RecuperacionRequest(usuario, correo))
                .onSuccess { _recuperarState.value = UiState.Success("Correo enviado exitosamente") }
                .onFailure { _recuperarState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
