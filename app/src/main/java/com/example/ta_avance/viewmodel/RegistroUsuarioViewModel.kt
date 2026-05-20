package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroUsuarioViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val registroExitoso = MutableLiveData<Boolean>()
    val mensajeError = MutableLiveData<String>()

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
            mensajeError.postValue("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            val request = LoginRequest(username, password, nombre, apellido, correo, celular)
            authRepository.register(request)
                .onSuccess { registroExitoso.postValue(true) }
                .onFailure { mensajeError.postValue(it.message) }
        }
    }
}