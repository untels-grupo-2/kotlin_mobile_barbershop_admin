package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListarUsuarioViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    val usuarios = MutableLiveData<List<LoginRequest>>()
    val error = MutableLiveData<String>()

    fun obtenerUsuarios() {
        viewModelScope.launch {
            usuarioRepository.listarUsuarios()
                .onSuccess { usuarios.postValue(it) }
                .onFailure { error.postValue(it.message) }
        }
    }
}