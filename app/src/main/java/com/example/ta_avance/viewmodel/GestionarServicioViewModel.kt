package com.example.ta_avance.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.servicio.ServicioDto
import com.example.ta_avance.dto.servicio.ServicioRequest
import com.example.ta_avance.repository.ServicioRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class GestionarServicioViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository
) : ViewModel() {

    val servicios = MutableLiveData<List<ServicioDto>>()
    val error = MutableLiveData<String>()
    val operacionExitosa = MutableLiveData<String>()

    fun obtenerServicios() {
        viewModelScope.launch {
            servicioRepository.listarServicios()
                .onSuccess { servicios.postValue(it) }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun crearServicio(context: Context, request: ServicioRequest, imagenUri: Uri?) {
        val dtoBody = buildJsonBody(request)
        val imagenPart = buildImagePart(context, imagenUri)

        viewModelScope.launch {
            servicioRepository.crearServicio(dtoBody, imagenPart)
                .onSuccess { operacionExitosa.postValue("Servicio creado exitosamente") }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun actualizarServicio(context: Context, id: Int, request: ServicioRequest, imagenUri: Uri?) {
        val dtoBody = buildJsonBody(request)
        val imagenPart = buildImagePart(context, imagenUri)

        viewModelScope.launch {
            servicioRepository.actualizarServicio(id, dtoBody, imagenPart)
                .onSuccess { operacionExitosa.postValue("Servicio actualizado exitosamente") }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun eliminarServicio(id: Int) {
        viewModelScope.launch {
            servicioRepository.eliminarServicio(id)
                .onSuccess { operacionExitosa.postValue("Servicio eliminado exitosamente") }
                .onFailure { error.postValue(it.message) }
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
            error.postValue("Error al procesar la imagen.")
            null
        }
    }
}