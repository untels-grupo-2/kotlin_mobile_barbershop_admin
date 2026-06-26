package com.example.ta_avance.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.servicio.ServicioDto
import com.example.ta_avance.dto.servicio.ServicioRequest
import com.example.ta_avance.repository.ServicioRepository
import com.example.ta_avance.ui.state.UiState
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
class GestionarServicioViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<UiState<List<ServicioDto>>>(UiState.Empty)
    val listState: StateFlow<UiState<List<ServicioDto>>> = _listState

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val operacionState: StateFlow<UiState<String>> = _operacionState

    fun obtenerServicios() {
        _listState.value = UiState.Loading
        viewModelScope.launch {
            servicioRepository.listarServicios()
                .onSuccess { _listState.value = UiState.Success(it) }
                .onFailure { _listState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun crearServicio(context: Context, request: ServicioRequest, imagenUri: Uri?) {
        val dtoBody = buildJsonBody(request)
        val imagenPart = buildImagePart(context, imagenUri) ?: return
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            servicioRepository.crearServicio(dtoBody, imagenPart)
                .onSuccess { _operacionState.value = UiState.Success("Servicio creado exitosamente") }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun actualizarServicio(context: Context, id: Int, request: ServicioRequest, imagenUri: Uri?) {
        val dtoBody = buildJsonBody(request)
        val imagenPart = buildImagePart(context, imagenUri)
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            servicioRepository.actualizarServicio(id, dtoBody, imagenPart)
                .onSuccess { _operacionState.value = UiState.Success("Servicio actualizado exitosamente") }
                .onFailure { _operacionState.value = UiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun eliminarServicio(id: Int) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            servicioRepository.eliminarServicio(id)
                .onSuccess { _operacionState.value = UiState.Success("Servicio eliminado exitosamente") }
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
