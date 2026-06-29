package com.example.ta_avance.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shared.models.dto.barbero.BarberoDto
import com.shared.models.dto.barbero.BarberoRequest
import com.example.ta_avance.repository.BarberoRepository
import com.shared.models.ui.state.UiState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class GestionarBarberoViewModel @Inject constructor(
    private val barberoRepository: BarberoRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<List<BarberoDto>>>(UiState.Empty)
    val listState: StateFlow<UiState<List<BarberoDto>>> = _listState

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val operacionState: StateFlow<UiState<String>> = _operacionState

    fun obtenerBarberos() {
        _listState.value = UiState.Loading
        viewModelScope.launch {
            barberoRepository.listarBarberos()
                .onSuccess { _listState.value = UiState.Success(it) }
                .onFailure { _listState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun crearBarbero(context: Context, nombre: String, imagenUri: Uri?) {
        if (nombre.isBlank()) {
            _operacionState.value = UiState.Error("El nombre del barbero es obligatorio")
            return
        }
        val dtoBody = buildJsonBody(BarberoRequest(nombre))
        val imagenPart = buildImagePart(context, imagenUri)
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            barberoRepository.crearBarbero(dtoBody, imagenPart)
                .onSuccess { _operacionState.value = UiState.Success("Barbero creado exitosamente") }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun actualizarBarbero(context: Context, id: Int, nuevoNombre: String, imagenUri: Uri?) {
        if (nuevoNombre.isBlank()) {
            _operacionState.value = UiState.Error("El nombre del barbero es obligatorio")
            return
        }
        val dtoBody = buildJsonBody(BarberoRequest(nuevoNombre))
        val imagenPart = buildImagePart(context, imagenUri)
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            barberoRepository.actualizarBarbero(id, dtoBody, imagenPart)
                .onSuccess { _operacionState.value = UiState.Success("Barbero actualizado exitosamente") }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun eliminarBarbero(id: Int) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            barberoRepository.eliminarBarbero(id)
                .onSuccess { _operacionState.value = UiState.Success("Barbero eliminado exitosamente") }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    private fun buildJsonBody(obj: Any): RequestBody {
        val json = Gson().toJson(obj)
        return RequestBody.create(MediaType.parse("application/json"), json)
    }

    private fun buildImagePart(context: Context, imagenUri: Uri?): MultipartBody.Part? {
        imagenUri ?: return null
        return try {
            val resolver = context.contentResolver
            val inputStream = resolver.openInputStream(imagenUri)!!
            val imageBytes = inputStream.readBytes()
            inputStream.close()
            val imagenBody = RequestBody.create(MediaType.parse(resolver.getType(imagenUri)), imageBytes)
            MultipartBody.Part.createFormData("imagen", "imagen.jpg", imagenBody)
        } catch (e: Exception) {
            _operacionState.value = UiState.Error("Error al procesar la imagen.")
            null
        }
    }
}
