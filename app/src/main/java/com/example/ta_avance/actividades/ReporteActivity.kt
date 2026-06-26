package com.example.ta_avance.actividades

import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ta_avance.R
import com.shared.models.dto.servicio.ServicioDto
import com.shared.models.ui.state.UiState
import com.example.ta_avance.viewmodel.GestionarServicioViewModel
import com.example.ta_avance.viewmodel.ReporteViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
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
    private lateinit var btnDescargarPdf: MaterialButton
    private lateinit var btnCompartirPdf: MaterialButton
    private lateinit var servicioViewModel: GestionarServicioViewModel
    private lateinit var reporteViewModel: ReporteViewModel

    private val listaServicios = mutableListOf<ServicioDto>()
    private var fechaInicioSeleccionada: LocalDate? = null
    private var fechaFinSeleccionada: LocalDate? = null
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var archivoPdf: File? = null

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
        btnDescargarPdf = findViewById(R.id.btnDescargarPdf)
        btnCompartirPdf = findViewById(R.id.btnCompartirPdf)

        servicioViewModel = ViewModelProvider(this).get(GestionarServicioViewModel::class.java)
        reporteViewModel = ViewModelProvider(this).get(ReporteViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                servicioViewModel.listState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            listaServicios.clear()
                            listaServicios.addAll(state.data)
                            val nombres = reporteViewModel.generarNombresServicios(state.data)
                            spinnerServicio.adapter = ArrayAdapter(this@ReporteActivity, android.R.layout.simple_spinner_dropdown_item, nombres)
                        }
                        is UiState.Error -> Toast.makeText(this@ReporteActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                reporteViewModel.reporteState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            val reporte = state.data
                            tvServicioNombre.text = reporte.servicioNombre ?: "Todos"
                            tvMontoTotal.text = "S/ ${reporte.montoTotal}"
                            tvCantidadReservas.text = "${reporte.cantidadReservas}"
                            cardResultado.visibility = View.VISIBLE
                            btnDescargarPdf.visibility = View.VISIBLE
                        }
                        is UiState.Error -> Toast.makeText(this@ReporteActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                reporteViewModel.descargaState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            btnDescargarPdf.isEnabled = false
                            btnDescargarPdf.text = "Descargando..."
                        }
                        is UiState.Success -> {
                            archivoPdf = state.data
                            btnDescargarPdf.isEnabled = true
                            btnDescargarPdf.text = "Descargar PDF"
                            btnCompartirPdf.visibility = View.VISIBLE
                            Toast.makeText(this@ReporteActivity, "PDF guardado en Descargas", Toast.LENGTH_SHORT).show()
                        }
                        is UiState.Error -> {
                            btnDescargarPdf.isEnabled = true
                            btnDescargarPdf.text = "Descargar PDF"
                            Toast.makeText(this@ReporteActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }

        etFechaInicio.setOnClickListener { mostrarDatePicker(true) }
        etFechaFin.setOnClickListener { mostrarDatePicker(false) }
        findViewById<Button>(R.id.btnFiltrarReporte).setOnClickListener { filtrarReporte() }

        btnDescargarPdf.setOnClickListener {
            reporteViewModel.descargarReporte(this, fechaInicioSeleccionada, fechaFinSeleccionada)
        }

        btnCompartirPdf.setOnClickListener {
            val archivo = archivoPdf ?: return@setOnClickListener
            val intent = reporteViewModel.construirIntentCompartir(this, archivo)
            startActivity(Intent.createChooser(intent, "Compartir reporte"))
        }

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
        cardResultado.visibility = View.GONE
        btnDescargarPdf.visibility = View.GONE
        btnCompartirPdf.visibility = View.GONE
        archivoPdf = null
        val servicioSeleccionado = reporteViewModel.resolverServicioSeleccionado(
            listaServicios,
            spinnerServicio.selectedItemPosition
        )
        reporteViewModel.obtenerReporte(fechaInicioSeleccionada, fechaFinSeleccionada, servicioSeleccionado)
    }
}
