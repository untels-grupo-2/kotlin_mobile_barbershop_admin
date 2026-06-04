package com.example.ta_avance.actividades

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.example.ta_avance.R
import com.example.ta_avance.dto.servicio.ServicioDto
import com.example.ta_avance.viewmodel.GestionarServicioViewModel
import com.example.ta_avance.viewmodel.ReporteViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class ReporteActivity : AppCompatActivity() {

    private lateinit var etFechaInicio: TextInputEditText
    private lateinit var etFechaFin: TextInputEditText
    private lateinit var spinnerServicio: Spinner
    private lateinit var tvServicioNombre: TextView
    private lateinit var tvMontoTotal: TextView
    private lateinit var tvCantidadReservas: TextView
    private lateinit var cardResultado: CardView
    private lateinit var servicioViewModel: GestionarServicioViewModel
    private lateinit var reporteViewModel: ReporteViewModel

    private val listaServicios = mutableListOf<ServicioDto>()
    private var fechaInicioSeleccionada: LocalDate? = null
    private var fechaFinSeleccionada: LocalDate? = null
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporte)

        etFechaInicio = findViewById(R.id.etFechaInicio)
        etFechaFin = findViewById(R.id.etFechaFin)
        spinnerServicio = findViewById(R.id.spinnerServicio)
        tvServicioNombre = findViewById(R.id.tvServicioNombre)
        tvMontoTotal = findViewById(R.id.tvMontoTotal)
        tvCantidadReservas = findViewById(R.id.tvCantidadReservas)
        cardResultado = findViewById(R.id.cardResultado)

        servicioViewModel = ViewModelProvider(this).get(GestionarServicioViewModel::class.java)
        reporteViewModel = ViewModelProvider(this).get(ReporteViewModel::class.java)

        servicioViewModel.servicios.observe(this) { servicios ->
            listaServicios.clear()
            listaServicios.addAll(servicios)
            val nombres = mutableListOf("Todos") + servicios.map { it.nombre }
            spinnerServicio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombres)
        }

        reporteViewModel.reporte.observe(this) { reporte ->
            tvServicioNombre.text = reporte.servicioNombre ?: "Todos"
            tvMontoTotal.text = "S/ ${reporte.montoTotal}"
            tvCantidadReservas.text = "${reporte.cantidadReservas}"
            cardResultado.visibility = View.VISIBLE
        }

        reporteViewModel.error.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        servicioViewModel.error.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        etFechaInicio.setOnClickListener { mostrarDatePicker(true) }
        etFechaFin.setOnClickListener { mostrarDatePicker(false) }
        findViewById<Button>(R.id.btnFiltrarReporte).setOnClickListener { filtrarReporte() }

        servicioViewModel.obtenerServicios()
    }

    private fun mostrarDatePicker(esInicio: Boolean) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            val fecha = LocalDate.of(y, m + 1, d)
            if (esInicio) {
                fechaInicioSeleccionada = fecha
                etFechaInicio.setText(formatter.format(fecha))
            } else {
                fechaFinSeleccionada = fecha
                etFechaFin.setText(formatter.format(fecha))
            }
        }, cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]).show()
    }

    private fun filtrarReporte() {
        if (fechaInicioSeleccionada == null || fechaFinSeleccionada == null) {
            Toast.makeText(this, "Selecciona ambas fechas", Toast.LENGTH_SHORT).show()
            return
        }

        val servicioSeleccionado = if (spinnerServicio.selectedItemPosition == 0) null
        else listaServicios[spinnerServicio.selectedItemPosition - 1].nombre

        cardResultado.visibility = View.GONE
        reporteViewModel.obtenerReporte(fechaInicioSeleccionada!!, fechaFinSeleccionada!!, servicioSeleccionado)
    }
}