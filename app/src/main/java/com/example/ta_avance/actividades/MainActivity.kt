package com.example.ta_avance.actividades

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ta_avance.R
import com.example.ta_avance.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

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
            val usuario = campoUsuario.text.toString()
            val contraseña = campoContraseña.text.toString()
            if (usuario.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                if (usuario.isEmpty()) campoUsuario.startAnimation(shakeAnimation)
                if (contraseña.isEmpty()) campoContraseña.startAnimation(shakeAnimation)
            } else {
                mainViewModel.login(usuario, contraseña)
            }
        }

        btnOlvideContrasena.setOnClickListener {
            startActivity(Intent(this, RecuperarContraActivity::class.java))
        }

        mainViewModel.loginStatus.observe(this) { status ->
            when (status) {
                "SUCCESS" -> {
                    val intent = Intent(this, AdminHomeActivity::class.java).apply {
                        putExtra("nombre", mainViewModel.nombre.value)
                        putExtra("apellido", mainViewModel.apellido.value)
                    }
                    startActivity(intent)
                    finish()
                }
                "NO_ADMIN" -> Toast.makeText(this, "Rol no autorizado", Toast.LENGTH_SHORT).show()
                "INVALID" -> Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Error: $status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}