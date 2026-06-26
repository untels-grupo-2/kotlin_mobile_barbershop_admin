package com.example.ta_avance.actividades

import android.os.Bundle
import android.widget.TextView
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
import com.example.ta_avance.adapters.ReservaAdapter
import com.example.ta_avance.dto.reserva.DtoReserva
import com.shared.models.ui.state.UiState
import com.example.ta_avance.viewmodel.ReservasViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReservasIdActivity : AppCompatActivity() {

    private lateinit var viewModel: ReservasViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tituloReservas: TextView
    private lateinit var tvMontoTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reservas_id)

        val usuarioId = intent.getLongExtra("usuarioId", -1)
        if (usuarioId == -1L) {
            Toast.makeText(this, "ID de usuario inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvMontoTotal = findViewById(R.id.tvMontoTotal)
        tituloReservas = findViewById(R.id.tituloReservas)
        recyclerView = findViewById(R.id.recyclerViewReservasId)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this).get(ReservasViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reservasState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            recyclerView.adapter = ReservaAdapter(state.data, object : ReservaAdapter.OnReservaClickListener {
                                override fun onVerDetallesClick(reserva: DtoReserva) {}
                                override fun onReservaRealizadaClick(reserva: DtoReserva) {}
                            }, "REALIZADA")

                            val (titulo, monto) = viewModel.calcularResumenReservas(state.data)
                            tituloReservas.text = titulo
                            tvMontoTotal.text = monto
                        }
                        is UiState.Error -> Toast.makeText(this@ReservasIdActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        viewModel.cargarReservasConId("", "REALIZADA", usuarioId)
    }
}
