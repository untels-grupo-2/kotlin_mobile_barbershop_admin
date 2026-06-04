package com.example.ta_avance.actividades

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.GridLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ta_avance.R
import com.example.ta_avance.viewmodel.AdminHomeViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminHomeActivity : AppCompatActivity() {

    private lateinit var viewModel: AdminHomeViewModel
    private lateinit var adminTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        viewModel = ViewModelProvider(this).get(AdminHomeViewModel::class.java)
        adminTitle = findViewById(R.id.adminTitle)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        viewModel.setNombreYApellido(nombre, apellido)

        viewModel.nombreCompleto.observe(this) { texto ->
            adminTitle.text = texto
        }

        findViewById<View>(R.id.listarUsuario).setOnClickListener {
            startActivity(Intent(this, ListarUsuarioActivity::class.java))
        }
        findViewById<View>(R.id.crearServicio).setOnClickListener {
            startActivity(Intent(this, GestionarServicioActivity::class.java))
        }
        findViewById<View>(R.id.gestionHorarios).setOnClickListener { v ->
            mostrarPopupHorarios(v)
        }
        findViewById<View>(R.id.gestionReservas).setOnClickListener {
            startActivity(Intent(this, ReservasActivity::class.java))
        }
        findViewById<View>(R.id.gestionBarbero).setOnClickListener {
            startActivity(Intent(this, GestionarBarberoActivity::class.java))
        }
        findViewById<View>(R.id.listarValoracion).setOnClickListener {
            startActivity(Intent(this, ListarValoracionActivity::class.java))
        }
        findViewById<View>(R.id.reportes).setOnClickListener {
            startActivity(Intent(this, ReporteActivity::class.java))
        }
        findViewById<View>(R.id.btnLogoutAdmin).setOnClickListener {
            viewModel.cerrarSesion()
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        ajustarBotonesGrid()
    }

    private fun mostrarPopupHorarios(anchorView: View) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_horarios, null)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        popupView.startAnimation(fadeIn)

        val dimBehind = View(this).apply {
            setBackgroundColor(0x88000000.toInt())
        }
        val rootView = window.decorView.rootView as ViewGroup
        rootView.addView(dimBehind, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        val popupWidth = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val popupWindow = PopupWindow(
            popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener { rootView.removeView(dimBehind) }

        popupView.findViewById<Button>(R.id.btnVerHorario).setOnClickListener {
            popupWindow.dismiss()
            Toast.makeText(this, "Ver horario actual", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HorarioActualActivity::class.java))
        }

        popupView.findViewById<Button>(R.id.btnPrepararHorario).setOnClickListener {
            popupWindow.dismiss()
            Toast.makeText(this, "Preparar horario", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HorarioPrepararActivity::class.java))
        }
    }

    private fun ajustarBotonesGrid() {
        val gridLayout = findViewById<GridLayout>(R.id.btnGrid)
        val totalBotones = gridLayout.childCount

        for (i in 0 until totalBotones) {
            val child = gridLayout.getChildAt(i)
            if (child is MaterialButton) {
                val params = child.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                params.setGravity(Gravity.FILL_HORIZONTAL)
                child.layoutParams = params
            }
        }

        if (totalBotones % 2 != 0) {
            val ultimo = gridLayout.getChildAt(totalBotones - 1)
            val params = ultimo.layoutParams as GridLayout.LayoutParams
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2)
            params.setGravity(Gravity.FILL_HORIZONTAL)
            ultimo.layoutParams = params
        }
    }
}