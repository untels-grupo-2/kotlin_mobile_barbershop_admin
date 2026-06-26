package com.example.ta_avance.actividades

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
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
import com.example.ta_avance.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var campoUsuario: EditText
    private lateinit var campoContraseña: EditText
    private lateinit var btnIngresarApp: Button
    private lateinit var btnOlvideContrasena: Button
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        campoUsuario = findViewById(R.id.campoUsuario)
        campoContraseña = findViewById(R.id.campoContraseña)
        btnIngresarApp = findViewById(R.id.btnIngresarApp)
        btnOlvideContrasena = findViewById(R.id.btnOlvideContrasena)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        findViewById<android.view.View>(R.id.diamondLogo).startAnimation(fadeIn)
        btnIngresarApp.startAnimation(fadeIn)
        btnOlvideContrasena.startAnimation(fadeIn)

        btnIngresarApp.setOnClickListener {
            mainViewModel.login(
                campoUsuario.text.toString(),
                campoContraseña.text.toString()
            )
        }

        btnOlvideContrasena.setOnClickListener {
            startActivity(Intent(this, RecuperarContraActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.loginState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            val intent = Intent(this@MainActivity, AdminHomeActivity::class.java).apply {
                                putExtra("nombre", state.data.first)
                                putExtra("apellido", state.data.second)
                            }
                            startActivity(intent)
                            finish()
                        }
                        is UiState.Error -> {
                            when (state.message) {
                                "CAMPOS_VACIOS" -> {
                                    Toast.makeText(this@MainActivity, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                                    if (campoUsuario.text.isBlank()) campoUsuario.startAnimation(shakeAnimation)
                                    if (campoContraseña.text.isBlank()) campoContraseña.startAnimation(shakeAnimation)
                                }
                                "NO_ADMIN" -> Toast.makeText(this@MainActivity, "Rol no autorizado", Toast.LENGTH_SHORT).show()
                                else -> Toast.makeText(this@MainActivity, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is UiState.Empty -> {}
                    }
                }
            }
        }
    }
}
