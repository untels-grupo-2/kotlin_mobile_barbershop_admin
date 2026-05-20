package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ta_avance.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    val nombreCompleto = MutableLiveData<String>()

    fun setNombreYApellido(nombre: String, apellido: String) {
        nombreCompleto.value = "Hola $nombre $apellido"
    }

    fun cerrarSesion() {
        preferenciasHelper.limpiarPreferencias()
    }
}