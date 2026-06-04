package com.example.ta_avance.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.repository.AuthRepository
import com.example.ta_avance.util.PreferenciasHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferenciasHelper: PreferenciasHelper
) : ViewModel() {

    val loginStatus = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val apellido = MutableLiveData<String>()

    fun login(usuario: String, contraseña: String) {
        viewModelScope.launch {
            val request = LoginRequest(username = usuario, password = contraseña)
            authRepository.login(request)
                .onSuccess { body ->
                    val token = body.data?.token ?: return@onSuccess
                    val refreshToken = body.data.refreshToken ?: return@onSuccess
                    val jwt = JWT(token)
                    val role = jwt.getClaim("rol").asString()

                    if (role != "ADMIN") {
                        loginStatus.postValue("NO_ADMIN")
                        return@onSuccess
                    }

                    preferenciasHelper.guardarToken(token)
                    preferenciasHelper.guardarRefreshToken(refreshToken)
                    nombre.postValue(jwt.getClaim("nombre").asString())
                    apellido.postValue(jwt.getClaim("apellido").asString())
                    loginStatus.postValue("SUCCESS")
                }
                .onFailure { e ->
                    loginStatus.postValue("FAILURE: ${e.message}")
                }
        }
    }
}