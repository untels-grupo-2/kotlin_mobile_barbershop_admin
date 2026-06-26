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
import com.shared.models.ui.state.UiState
import com.example.ta_avance.viewmodel.RecuperarContraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecuperarContraActivity : AppCompatActivity() {

    private lateinit var campoUsuario: EditText
    private lateinit var campoCorreo: EditText
    private lateinit var viewModel: RecuperarContraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)

        campoUsuario = findViewById(R.id.campoUsuario)
        campoCorreo = findViewById(R.id.campoEmail)
        viewModel = ViewModelProvider(this).get(RecuperarContraViewModel::class.java)

        findViewById<Button>(R.id.btnRecuperarContra).setOnClickListener {
            viewModel.recuperar(
                campoUsuario.text.toString().trim(),
                campoCorreo.text.toString().trim()
            )
        }

        findViewById<Button>(R.id.btnVolverLogin).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
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
