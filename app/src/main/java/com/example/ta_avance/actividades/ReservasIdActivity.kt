package com.example.ta_avance.actividades

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.ReservaAdapter
import com.example.ta_avance.dto.reserva.DtoReserva
import com.example.ta_avance.viewmodel.ReservasViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservasIdActivity : AppCompatActivity() {

    private lateinit var viewModel: ReservasViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tituloReservas: TextView
    private lateinit var tvMontoTotal: TextView
    private var usuarioId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reservas_id)

        usuarioId = intent.getLongExtra("usuarioId", -1)
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
        cargarReservas()
    }

    private fun cargarReservas() {
        val estado = "REALIZADA"

        viewModel.reservas.observe(this) { reservas ->
            recyclerView.adapter = ReservaAdapter(reservas, object : ReservaAdapter.OnReservaClickListener {
                override fun onVerDetallesClick(reserva: DtoReserva) {}
                override fun onReservaRealizadaClick(reserva: DtoReserva) {}
            }, estado)

            if (reservas.isNotEmpty()) {
                tvMontoTotal.text = "Monto Total: S/ ${reservas[0].montoTotal}"
                tituloReservas.text = "Reservas de ${reservas[0].usuarioNombre}"
            } else {
                tvMontoTotal.text = "Monto Total: S/ 0"
                tituloReservas.text = "Reservas del cliente"
            }
        }

        viewModel.cargarReservasConId("", estado, usuarioId)
    }
}