package com.example.ta_avance.actividades

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ta_avance.R
import com.example.ta_avance.adapters.ReservaAdapter
import com.example.ta_avance.dto.login.LoginRequest
import com.example.ta_avance.dto.reserva.DtoReserva
import com.example.ta_avance.viewmodel.ReservasViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@AndroidEntryPoint
class ReservasActivity : AppCompatActivity() {

    private lateinit var viewModel: ReservasViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var etFechaSeleccionada: TextInputEditText
    private lateinit var spinnerEstado: Spinner
    private lateinit var tituloReservas: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        val nuevoLocale = Locale("es", "ES")
        Locale.setDefault(nuevoLocale)
        val config = resources.configuration
        config.setLocale(nuevoLocale)
        resources.updateConfiguration(config, resources.displayMetrics)

        recyclerView = findViewById(R.id.recyclerViewReservas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        spinnerEstado = findViewById(R.id.spinnerEstado)
        etFechaSeleccionada = findViewById(R.id.etFechaSeleccionada)
        tituloReservas = findViewById(R.id.tituloReservas)

        val estados = arrayOf("CREADA", "CONFIRMADA", "REALIZADA")
        spinnerEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        viewModel = ViewModelProvider(this).get(ReservasViewModel::class.java)

        viewModel.reservas.observe(this) { reservas ->
            val estado = spinnerEstado.selectedItem.toString()
            recyclerView.adapter = ReservaAdapter(reservas, object : ReservaAdapter.OnReservaClickListener {
                override fun onVerDetallesClick(reserva: DtoReserva) {
                    mostrarPopupDetalle(reserva)
                }
                override fun onReservaRealizadaClick(reserva: DtoReserva) {
                    viewModel.cambiarEstadoReserva(reserva.reservaId, "REALIZADA", "Reserva completada correctamente")
                }
            }, estado)
        }

        viewModel.mensajeError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.cambioEstadoExitoso.observe(this) { exitoso ->
            if (exitoso == true) {
                Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show()
                cargarReservas()
            }
        }

        spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                etFechaSeleccionada.setText("")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        etFechaSeleccionada.setOnClickListener { mostrarDatePicker() }
        findViewById<Button>(R.id.btnFiltrar).setOnClickListener { cargarReservas() }
    }

    private fun mostrarDatePicker() {
        val estadoSeleccionado = spinnerEstado.selectedItem.toString()
        val hoy = LocalDate.now()
        val builder = MaterialDatePicker.Builder.datePicker().setTitleText("Selecciona una fecha")

        when (estadoSeleccionado) {
            "CREADA", "CONFIRMADA" -> {
                val finSemana = hoy.with(DayOfWeek.SUNDAY)
                val fechaMin = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val fechaMax = finSemana.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                builder.setSelection(fechaMin).setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setStart(fechaMin).setEnd(fechaMax)
                        .setValidator(CompositeDateValidator.allOf(listOf(
                            DateValidatorPointForward.from(fechaMin),
                            DateValidatorPointBackward.before(fechaMax)
                        ))).build()
                )
            }
            "REALIZADA" -> {
                val fechaMax = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                builder.setSelection(fechaMax).setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setEnd(fechaMax)
                        .setValidator(DateValidatorPointBackward.before(fechaMax))
                        .build()
                )
            }
        }

        builder.build().apply {
            addOnPositiveButtonClickListener { selection ->
                val fechaSeleccionada = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                etFechaSeleccionada.setText(fechaSeleccionada.toString())
            }
            show(supportFragmentManager, "DATE_PICKER")
        }
    }

    private fun cargarReservas() {
        val fecha = etFechaSeleccionada.text.toString()
        val estado = spinnerEstado.selectedItem.toString()

        if ((estado == "CREADA" || estado == "CONFIRMADA") && fecha.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        tituloReservas.text = "Reservas - $estado"
        viewModel.cargarReservas(fecha, estado)
    }

    private fun mostrarPopupDetalle(reserva: DtoReserva) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_reserva_detalle, null)
        val dialog = AlertDialog.Builder(this).setView(popupView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        popupView.findViewById<TextView>(R.id.tvDetalleUsuario).text = "Usuario: ${reserva.usuarioNombre}"
        popupView.findViewById<TextView>(R.id.tvDetalleBarbero).text = "Barbero: ${reserva.barberoNombre}"
        popupView.findViewById<TextView>(R.id.tvDetalleHorario).text = "Horario: ${reserva.horarioRango}"
        popupView.findViewById<TextView>(R.id.tvDetalleServicio).text = "Servicio: ${reserva.servicioNombre}"
        popupView.findViewById<TextView>(R.id.tvDetalleFechaReserva).text = "Fecha reserva: ${reserva.fechaReserva}"
        popupView.findViewById<TextView>(R.id.tvDetalleFechaCreacion).text = "Creado el: ${reserva.fechaCreacion}"
        popupView.findViewById<TextView>(R.id.tvDetallePrecio).text = "Precio: s/${reserva.precioServicio}"
        popupView.findViewById<TextView>(R.id.tvDetalleAdicionales).text = "Adicionales: ${reserva.adicionales}"

        val ivComprobante = popupView.findViewById<ImageView>(R.id.ivComprobante)
        if (!reserva.urlPago.isNullOrEmpty()) {
            Glide.with(this).load(reserva.urlPago).into(ivComprobante)
        } else {
            ivComprobante.visibility = View.GONE
        }

        popupView.findViewById<MaterialButton>(R.id.btnConfirmarReserva).setOnClickListener {
            dialog.dismiss()
            viewModel.obtenerUsuarioPorId(reserva.usuarioId)
            viewModel.usuarioPorIdLiveData.observe(this) { usuario ->
                if (usuario != null) {
                    enviarWsp(usuario, reserva)
                    viewModel.cambiarEstadoReserva(reserva.reservaId, "CONFIRMADA", "Reserva confirmada por el administrador.")
                }
            }
        }

        popupView.findViewById<MaterialButton>(R.id.btnCancelarReserva).setOnClickListener {
            dialog.dismiss()
            mostrarPopupMotivo(reserva.reservaId, "CANCELADA")
        }

        dialog.show()
    }

    private fun mostrarPopupMotivo(reservaId: Long, nuevoEstado: String) {
        val motivoView = LayoutInflater.from(this).inflate(R.layout.popup_motivo_cancelacion, null)
        val motivoDialog = AlertDialog.Builder(this).setView(motivoView).create()

        val etMotivoCancelacion = motivoView.findViewById<EditText>(R.id.etMotivoCancelacion)
        motivoView.findViewById<MaterialButton>(R.id.btnEnviarMotivo).setOnClickListener {
            viewModel.cambiarEstadoReserva(reservaId, nuevoEstado, etMotivoCancelacion.text.toString().trim())
            motivoDialog.dismiss()
        }

        motivoDialog.show()
    }

    private fun enviarWsp(usuario: LoginRequest, reserva: DtoReserva) {
        val mensaje = """
            Hola *${usuario.nombre}*, tu reserva ha sido confirmada 🎉
            
            📅 Fecha: *${reserva.fechaReserva}*
            🕒 Horario: *${reserva.horarioRango}*
            💇 Servicio: *${reserva.servicioNombre}*
            
            Por favor, acude puntual a tu cita. ¡Gracias por elegirnos! 💈
        """.trimIndent()

        try {
            val uri = "https://wa.me/51${usuario.celular}?text=${URLEncoder.encode(mensaje, "UTF-8")}"
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(uri) })
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}