package com.example.ta_avance.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ta_avance.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    private val _nombreCompleto = MutableStateFlow("")
    val nombreCompleto: StateFlow<String> = _nombreCompleto

    fun setNombreYApellido(nombre: String, apellido: String) {
        _nombreCompleto.value = "Hola $nombre $apellido"
    }

    fun cerrarSesion() {
        preferenciasHelper.limpiarPreferencias()
    }
}
