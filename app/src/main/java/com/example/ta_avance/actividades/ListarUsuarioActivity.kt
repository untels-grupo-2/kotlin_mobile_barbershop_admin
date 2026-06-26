package com.example.ta_avance.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.UsuarioAdapter
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.ui.state.UiState
import com.example.ta_avance.viewmodel.ListarUsuarioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usuariosState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            recyclerView.adapter = UsuarioAdapter(state.data, object : UsuarioAdapter.OnUsuarioClickListener {
                                override fun onMessageWsp(usuario: LoginRequest) {
                                    abrirWhatsApp(viewModel.generarUriWhatsAppBienvenida(usuario))
                                }
                                override fun onVerReservas(usuario: LoginRequest) {
                                    startActivity(Intent(this@ListarUsuarioActivity, ReservasIdActivity::class.java).apply {
                                        putExtra("usuarioId", usuario.usuario_id)
                                    })
                                }
                            })
                        }
                        is UiState.Error -> Toast.makeText(this@ListarUsuarioActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        viewModel.obtenerUsuarios()
    }

    private fun abrirWhatsApp(uri: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(uri) })
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
