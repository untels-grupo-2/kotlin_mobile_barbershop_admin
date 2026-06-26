package com.example.ta_avance.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ta_avance.R
import com.example.ta_avance.ui.state.UiState
import com.example.ta_avance.viewmodel.RecuperarContraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecuperarContraActivity : AppCompatActivity() {

    private lateinit var campoUsuario: EditText
    private lateinit var campoCorreo: EditText
    private lateinit var btnRecuperarContra: Button
    private lateinit var btnVolverLogin: Button
    private lateinit var viewModel: RecuperarContraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)

        campoUsuario = findViewById(R.id.campoUsuario)
        campoCorreo = findViewById(R.id.campoEmail)
        btnRecuperarContra = findViewById(R.id.btnRecuperarContra)
        btnVolverLogin = findViewById(R.id.btnVolverLogin)

        viewModel = ViewModelProvider(this).get(RecuperarContraViewModel::class.java)

        btnRecuperarContra.setOnClickListener {
            val usuario = campoUsuario.text.toString().trim()
            val correo = campoCorreo.text.toString().trim()
            if (usuario.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.recuperar(usuario, correo)
        }

        btnVolverLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recuperarState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            Toast.makeText(this@RecuperarContraActivity, state.data, Toast.LENGTH_LONG).show()
                            finish()
                        }
                        is UiState.Error -> Toast.makeText(this@RecuperarContraActivity, state.message, Toast.LENGTH_LONG).show()
                        else -> {}
                    }
                }
            }
        }
    }
}
