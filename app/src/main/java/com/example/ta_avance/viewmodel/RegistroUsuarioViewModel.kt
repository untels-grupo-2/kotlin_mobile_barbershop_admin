package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.repository.AuthRepository
import com.example.ta_avance.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroUsuarioViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registroState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val registroState: StateFlow<UiState<Boolean>> = _registroState

    fun registrarUsuario(
        username: String,
        password: String,
        nombre: String,
        apellido: String,
        correo: String,
        celular: String
    ) {
        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() ||
            apellido.isEmpty() || correo.isEmpty() || celular.isEmpty()) {
            _registroState.value = UiState.Error("Por favor, completa todos los campos")
            return
        }

        _registroState.value = UiState.Loading
        viewModelScope.launch {
            val request = LoginRequest(username, password, nombre, apellido, correo, celular)
            authRepository.register(request)
                .onSuccess { _registroState.value = UiState.Success(true) }
                .onFailure { _registroState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
