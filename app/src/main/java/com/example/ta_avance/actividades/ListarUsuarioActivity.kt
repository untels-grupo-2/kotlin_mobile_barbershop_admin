package com.example.ta_avance.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.UsuarioAdapter
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.viewmodel.ListarUsuarioViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class ListarUsuarioActivity : AppCompatActivity() {

    private lateinit var viewModel: ListarUsuarioViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_usuario)

        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this).get(ListarUsuarioViewModel::class.java)

        findViewById<Button>(R.id.btnAgregarUsuario).setOnClickListener {
            startActivity(Intent(this, RegistroUsuarioActivity::class.java))
        }

        viewModel.usuarios.observe(this) { usuarios ->
            recyclerView.adapter = UsuarioAdapter(usuarios, object : UsuarioAdapter.OnUsuarioClickListener {
                override fun onMessageWsp(usuario: LoginRequest) {
                    enviarWsp(usuario)
                }
                override fun onVerReservas(usuario: LoginRequest) {
                    val intent = Intent(this@ListarUsuarioActivity, ReservasIdActivity::class.java).apply {
                        putExtra("usuarioId", usuario.usuario_id)
                    }
                    startActivity(intent)
                }
            })
        }

        viewModel.error.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.obtenerUsuarios()
    }

    private fun enviarWsp(usuario: LoginRequest) {
        val mensaje = """
            Hola *${usuario.nombre}*, tu cuenta ha sido creada. Aquí tienes tus credenciales:
            
            👤 Usuario: *${usuario.username}*
            🔑 Contraseña: *123456789*
            
            📲 Descarga la app desde aquí: https://pagina-barbershop.vercel.app/
            Por favor, cambia tu contraseña después de ingresar.
        """.trimIndent()

        try {
            val uri = "https://wa.me/51${usuario.celular}?text=${URLEncoder.encode(mensaje, "UTF-8")}"
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(uri) })
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}