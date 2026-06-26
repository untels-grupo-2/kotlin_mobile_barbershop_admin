package com.example.ta_avance.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.ValoracionAdapter
import com.example.ta_avance.dto.valoracion.ValoracionDto
import com.example.ta_avance.ui.state.UiState
import com.example.ta_avance.viewmodel.ListarValoracionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListarValoracionActivity : AppCompatActivity() {

    private lateinit var viewModel: ListarValoracionViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_valoracion)

        recyclerView = findViewById(R.id.recyclerViewValoraciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this).get(ListarValoracionViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.valoracionesState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            recyclerView.adapter = ValoracionAdapter(state.data) { valoracion ->
                                abrirWhatsApp(viewModel.generarUriWhatsAppValoracion(valoracion))
                                viewModel.responderValoracion(valoracion.valoracion_id)
                            }
                        }
                        is UiState.Error -> Toast.makeText(this@ListarValoracionActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.operacionState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            Toast.makeText(this@ListarValoracionActivity, state.data, Toast.LENGTH_SHORT).show()
                            viewModel.obtenerValoraciones()
                        }
                        is UiState.Error -> Toast.makeText(this@ListarValoracionActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        viewModel.obtenerValoraciones()
    }

    private fun abrirWhatsApp(uri: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(uri) })
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
