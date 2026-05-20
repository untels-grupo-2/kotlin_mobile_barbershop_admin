package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.recuperacion.RecuperacionRequest
import com.example.ta_avance.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecuperarContraViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val resultado = MutableLiveData<String>()
    val error = MutableLiveData<String>()

    fun recuperar(usuario: String, correo: String) {
        viewModelScope.launch {
            authRepository.recuperarContraseña(RecuperacionRequest(usuario, correo))
                .onSuccess { resultado.postValue("Correo enviado exitosamente") }
                .onFailure { error.postValue(it.message) }
        }
    }
}