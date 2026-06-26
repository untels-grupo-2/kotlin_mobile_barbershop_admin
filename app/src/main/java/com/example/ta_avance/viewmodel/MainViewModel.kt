package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.repository.AuthRepository
import com.shared.models.ui.state.UiState
import com.example.ta_avance.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Pair<String, String>>>(UiState.Empty)
    val loginState: StateFlow<UiState<Pair<String, String>>> = _loginState

    fun login(usuario: String, contraseña: String) {
        if (usuario.isBlank() || contraseña.isBlank()) {
            _loginState.value = UiState.Error("CAMPOS_VACIOS")
            return
        }

        _loginState.value = UiState.Loading
        viewModelScope.launch {
            val request = LoginRequest(username = usuario, password = contraseña)
            authRepository.login(request)
                .onSuccess { body ->
                    val token = body.data?.token ?: run {
                        _loginState.value = UiState.Error("Token inválido")
                        return@onSuccess
                    }
                    val refreshToken = body.data.refreshToken ?: run {
                        _loginState.value = UiState.Error("Refresh token inválido")
                        return@onSuccess
                    }
                    val jwt = JWT(token)
                    val role = jwt.getClaim("rol").asString()

                    if (role != "ADMIN") {
                        _loginState.value = UiState.Error("NO_ADMIN")
                        return@onSuccess
                    }

                    preferenciasHelper.guardarToken(token)
                    preferenciasHelper.guardarRefreshToken(refreshToken)

                    val nombre = jwt.getClaim("nombre").asString() ?: ""
                    val apellido = jwt.getClaim("apellido").asString() ?: ""
                    _loginState.value = UiState.Success(Pair(nombre, apellido))
                }
                .onFailure { e ->
                    _loginState.value = UiState.Error(e.message ?: "Error desconocido")
                }
        }
    }
}
