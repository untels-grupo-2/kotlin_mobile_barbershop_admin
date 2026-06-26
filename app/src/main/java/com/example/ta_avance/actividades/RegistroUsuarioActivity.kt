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
import com.example.ta_avance.viewmodel.RegistroUsuarioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var usuarioInput: EditText
    private lateinit var contraseñaInput: EditText
    private lateinit var nombreInput: EditText
    private lateinit var apellidoInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var celularInput: EditText
    private lateinit var viewModel: RegistroUsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        usuarioInput = findViewById(R.id.usuarioInput)
        contraseñaInput = findViewById(R.id.contraseñaInput)
        nombreInput = findViewById(R.id.nombreInput)
        apellidoInput = findViewById(R.id.apellidoInput)
        correoInput = findViewById(R.id.correoInput)
        celularInput = findViewById(R.id.celularInput)
        val registrarButton = findViewById<Button>(R.id.registrarButton)
        val btnVolverHome = findViewById<Button>(R.id.volverButton)

        viewModel = ViewModelProvider(this).get(RegistroUsuarioViewModel::class.java)

        registrarButton.setOnClickListener {
            val username = usuarioInput.text.toString().trim()
            val password = contraseñaInput.text.toString().trim()
            val nombre = nombreInput.text.toString().trim()
            val apellido = apellidoInput.text.toString().trim()
            val correo = correoInput.text.toString().trim()
            val celular = celularInput.text.toString().trim()
            viewModel.registrarUsuario(username, password, nombre, apellido, correo, celular)
        }

        btnVolverHome.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registroState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            Toast.makeText(this@RegistroUsuarioActivity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegistroUsuarioActivity, ListarUsuarioActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                        is UiState.Error -> Toast.makeText(this@RegistroUsuarioActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }
    }
}
