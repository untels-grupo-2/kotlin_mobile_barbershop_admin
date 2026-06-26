package com.example.ta_avance.actividades

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ta_avance.R
import com.example.ta_avance.ui.state.UiState
import com.example.ta_avance.viewmodel.HorarioActualViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HorarioActualActivity : AppCompatActivity() {

    private lateinit var viewModel: HorarioActualViewModel
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horario_actual)

        container = findViewById(R.id.containerDiasHorarioActual)
        viewModel = ViewModelProvider(this).get(HorarioActualViewModel::class.java)

        val ordenDias = arrayOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
        val ordenTurnos = arrayOf("MAÑANA", "TARDE", "NOCHE")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.horariosState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            val semana = state.data
                            container.removeAllViews()

                            for (dia in ordenDias) {
                                if (!semana.containsKey(dia)) continue

                                val diaButton = MaterialButton(this@HorarioActualActivity).apply {
                                    text = dia
                                    setIcon(ContextCompat.getDrawable(this@HorarioActualActivity, R.drawable.ic_calendar_day))
                                    setIconTintResource(android.R.color.white)
                                    iconPadding = 16
                                    setTextColor(ContextCompat.getColor(this@HorarioActualActivity, android.R.color.white))
                                    isAllCaps = false
                                    typeface = ResourcesCompat.getFont(this@HorarioActualActivity, R.font.oswald_bold)
                                    backgroundTintList = ContextCompat.getColorStateList(this@HorarioActualActivity, R.color.barber_black_deep)
                                    cornerRadius = 24
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply { setMargins(0, 16, 0, 0) }
                                }

                                val turnosLayout = LinearLayout(this@HorarioActualActivity).apply {
                                    orientation = LinearLayout.VERTICAL
                                    visibility = View.GONE
                                    setPadding(32, 12, 12, 12)
                                    background = ContextCompat.getDrawable(this@HorarioActualActivity, R.drawable.bg_turno_card)
                                    elevation = 4f
                                }

                                val turnos = semana[dia]!!
                                for (turno in ordenTurnos) {
                                    if (turnos.containsKey(turno)) {
                                        val barberosTexto = turnos[turno]!!.joinToString(", ")
                                        val turnoView = TextView(this@HorarioActualActivity).apply {
                                            text = "$turno: $barberosTexto"
                                            textSize = 15f
                                            setTextColor(ContextCompat.getColor(this@HorarioActualActivity, R.color.barber_black_deep))
                                            typeface = ResourcesCompat.getFont(this@HorarioActualActivity, R.font.oswald_regular)
                                            setPadding(12, 8, 12, 8)
                                        }
                                        turnosLayout.addView(turnoView)
                                    }
                                }

                                diaButton.setOnClickListener {
                                    turnosLayout.visibility = if (turnosLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                                }

                                container.addView(diaButton)
                                container.addView(turnosLayout)
                            }
                        }
                        is UiState.Error -> Toast.makeText(this@HorarioActualActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exportState.collect { state ->
                    if (state is UiState.Error) {
                        Toast.makeText(this@HorarioActualActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnExportarHorarioSemanal).setOnClickListener {
            viewModel.exportarHorario(this)
        }

        viewModel.cargarHorarios()
    }
}
