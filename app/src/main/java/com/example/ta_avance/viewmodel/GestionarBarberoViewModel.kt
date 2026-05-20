package com.example.ta_avance.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ta_avance.dto.barbero.BarberoDto
import com.example.ta_avance.dto.barbero.BarberoRequest
import com.example.ta_avance.repository.BarberoRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class GestionarBarberoViewModel @Inject constructor(
    private val barberoRepository: BarberoRepository
) : ViewModel() {

    val barberos = MutableLiveData<List<BarberoDto>>()
    val error = MutableLiveData<String>()
    val operacionExitosa = MutableLiveData<String>()

    fun obtenerBarberos() {
        viewModelScope.launch {
            barberoRepository.listarBarberos()
                .onSuccess { barberos.postValue(it) }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun crearBarbero(context: Context, nombre: String, imagenUri: Uri?) {
        val dtoBody = buildJsonBody(BarberoRequest(nombre))
        val imagenPart = buildImagePart(context, imagenUri)

        viewModelScope.launch {
            barberoRepository.crearBarbero(dtoBody, imagenPart)
                .onSuccess { operacionExitosa.postValue("Barbero creado exitosamente") }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun actualizarBarbero(context: Context, id: Int, nuevoNombre: String, imagenUri: Uri?) {
        val dtoBody = buildJsonBody(BarberoRequest(nuevoNombre))
        val imagenPart = buildImagePart(context, imagenUri)

        viewModelScope.launch {
            barberoRepository.actualizarBarbero(id, dtoBody, imagenPart)
                .onSuccess { operacionExitosa.postValue("Barbero actualizado exitosamente") }
                .onFailure { error.postValue(it.message) }
        }
    }

    fun eliminarBarbero(id: Int) {
        viewModelScope.launch {
            barberoRepository.eliminarBarbero(id)
                .onSuccess { operacionExitosa.postValue("Barbero eliminado exitosamente") }
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