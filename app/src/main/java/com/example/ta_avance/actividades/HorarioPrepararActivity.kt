package com.example.ta_avance.actividades

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ta_avance.R
import com.example.ta_avance.viewmodel.HorarioPrepararViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HorarioPrepararActivity : AppCompatActivity() {

    private lateinit var viewModel: HorarioPrepararViewModel
    private lateinit var containerDias: LinearLayout

    companion object {
        private val turnoIds = mapOf("MAÑANA" to 1L, "TARDE" to 2L, "NOCHE" to 3L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario_preparar)

        containerDias = findViewById(R.id.containerDias)
        viewModel = ViewModelProvider(this).get(HorarioPrepararViewModel::class.java)

        viewModel.dias.observe(this) { dias ->
            containerDias.removeAllViews()
            for (dia in dias) {
                val diaButton = MaterialButton(this).apply {
                    text = dia
                    setIcon(ContextCompat.getDrawable(this@HorarioPrepararActivity, R.drawable.ic_calendar_day))
                    setIconTintResource(android.R.color.white)
                    iconPadding = 16
                    setTextColor(ContextCompat.getColor(this@HorarioPrepararActivity, android.R.color.white))
                    isAllCaps = false
                    typeface = ResourcesCompat.getFont(this@HorarioPrepararActivity, R.font.oswald_bold)
                    backgroundTintList = ContextCompat.getColorStateList(this@HorarioPrepararActivity, R.color.barber_black_deep)
                    cornerRadius = 24
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 16, 0, 0) }
                    setOnClickListener { mostrarPopupDia(dia) }
                }
                containerDias.addView(diaButton)
            }
        }

        viewModel.operacionExitosa.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.error.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnConfirmarHorario).setOnClickListener {
            viewModel.confirmarHorario()
        }

        findViewById<Button>(R.id.btnExportarHorario).setOnClickListener {
            viewModel.exportarHorario(this)
        }

        viewModel.cargarBarberos()
    }

    private fun mostrarPopupDia(dia: String) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_preparar_dia, null)
        val tituloDia = popupView.findViewById<TextView>(R.id.tituloDia)
        val contenedorTurnos = popupView.findViewById<LinearLayout>(R.id.contenedorTurnos)
        val btnGuardar = popupView.findViewById<Button>(R.id.btnGuardar)

        tituloDia.text = "Preparación $dia"
        contenedorTurnos.removeAllViews()

        val turnos = viewModel.turnos.value
        val barberos = viewModel.barberos.value

        if (turnos == null || barberos == null) {
            Toast.makeText(this, "Error cargando turnos o barberos", Toast.LENGTH_SHORT).show()
            return
        }

        val layoutsPorTurno = mutableMapOf<String, LinearLayout>()

        for (turno in turnos) {
            contenedorTurnos.addView(TextView(this).apply {
                text = turno
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 20, 0, 10)
            })

            val layoutBarberos = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            for (barbero in barberos) {
                layoutBarberos.addView(CheckBox(this).apply {
                    text = barbero.nombre
                    tag = barbero.barbero_id
                })
            }

            contenedorTurnos.addView(layoutBarberos)
            layoutsPorTurno[turno] = layoutBarberos
        }

        val rootView = window.decorView.rootView as ViewGroup
        val fondoOscuro = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        rootView.addView(fondoOscuro, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val popupWindow = PopupWindow(
            popupView,
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.setOnDismissListener { rootView.removeView(fondoOscuro) }
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)

        btnGuardar.setOnClickListener {
            val turnosPorTipo = mutableMapOf<Long, List<Long>>()

            for (turno in turnos) {
                val layout = layoutsPorTurno[turno] ?: continue
                val barberoIdsSeleccionados = (0 until layout.childCount)
                    .map { layout.getChildAt(it) }
                    .filterIsInstance<CheckBox>()
                    .filter { it.isChecked }
                    .map { (it.tag as Int).toLong() }

                turnoIds[turno.uppercase()]?.let { turnoId ->
                    turnosPorTipo[turnoId] = barberoIdsSeleccionados
                }
            }

            viewModel.guardarTurnosDia(dia, turnosPorTipo)
            popupWindow.dismiss()
        }
    }
}