package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.repository.UsuarioRepository
import com.example.ta_avance.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarUsuarioViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _usuariosState = MutableStateFlow<UiState<List<LoginRequest>>>(UiState.Empty)
    val usuariosState: StateFlow<UiState<List<LoginRequest>>> = _usuariosState

    fun obtenerUsuarios() {
        _usuariosState.value = UiState.Loading
        viewModelScope.launch {
            usuarioRepository.listarUsuarios()
                .onSuccess { _usuariosState.value = UiState.Success(it) }
                .onFailure { _usuariosState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
