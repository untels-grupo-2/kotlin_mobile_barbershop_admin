package com.example.ta_avance.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.BarberoAdapter
import com.example.ta_avance.dto.barbero.BarberoDto
import com.example.ta_avance.ui.state.UiState
import com.example.ta_avance.viewmodel.GestionarBarberoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GestionarBarberoActivity : AppCompatActivity() {

    private lateinit var viewModel: GestionarBarberoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAgregarBarbero: Button
    private var imagenSeleccionadaUri: Uri? = null

    companion object {
        private const val REQUEST_SELECT_IMAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_barbero)

        recyclerView = findViewById(R.id.recyclerViewBarberos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnAgregarBarbero = findViewById(R.id.btnAgregarBarbero)
        btnAgregarBarbero.setOnClickListener { mostrarPopupNuevoBarbero(it) }

        viewModel = ViewModelProvider(this).get(GestionarBarberoViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            recyclerView.adapter = BarberoAdapter(state.data, object : BarberoAdapter.OnBarberoClickListener {
                                override fun onActualizar(barbero: BarberoDto) {
                                    mostrarPopupActualizarBarbero(barbero)
                                }
                                override fun onEliminar(barbero: BarberoDto) {
                                    eliminarBarbero(barbero.barbero_id)
                                }
                            })
                        }
                        is UiState.Error -> Toast.makeText(this@GestionarBarberoActivity, state.message, Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@GestionarBarberoActivity, state.data, Toast.LENGTH_SHORT).show()
                            cargarLista()
                        }
                        is UiState.Error -> Toast.makeText(this@GestionarBarberoActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        cargarLista()
    }

    private fun cargarLista() {
        viewModel.obtenerBarberos()
    }

    private fun mostrarPopupActualizarBarbero(barbero: BarberoDto) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_barbero, null)
        val etNombre = popupView.findViewById<EditText>(R.id.etNombreNuevoBarbero)
        val btnCrear = popupView.findViewById<Button>(R.id.btnCrearBarbero)
        val btnCancelar = popupView.findViewById<Button>(R.id.btnCancelar)
        val btnSeleccionarImagen = popupView.findViewById<Button>(R.id.btnSeleccionarImagen)

        btnSeleccionarImagen.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }

        etNombre.setText(barbero.nombre)
        btnCrear.text = "Actualizar"

        val rootView = window.decorView.rootView as ViewGroup
        val dimBehind = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        rootView.addView(dimBehind, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener { rootView.removeView(dimBehind) }

        btnCrear.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            if (nuevoNombre.isNotEmpty()) {
                viewModel.actualizarBarbero(this, barbero.barbero_id, nuevoNombre, imagenSeleccionadaUri)
                popupWindow.dismiss()
            } else {
                etNombre.error = "Campo obligatorio"
            }
        }

        btnCancelar.setOnClickListener { popupWindow.dismiss() }
    }

    private fun mostrarPopupNuevoBarbero(anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_barbero, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        val rootView = window.decorView.rootView as ViewGroup
        val dimBehind = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        rootView.addView(dimBehind, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener { rootView.removeView(dimBehind) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreNuevoBarbero)
        val btnCrear = popupView.findViewById<Button>(R.id.btnCrearBarbero)
        val btnCancelar = popupView.findViewById<Button>(R.id.btnCancelar)
        val btnSeleccionarImagen = popupView.findViewById<Button>(R.id.btnSeleccionarImagen)

        btnSeleccionarImagen.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }

        btnCrear.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isNotEmpty()) {
                viewModel.crearBarbero(this, nombre, imagenSeleccionadaUri)
                imagenSeleccionadaUri = null
                popupWindow.dismiss()
            } else {
                etNombre.error = "Campo obligatorio"
            }
        }

        btnCancelar.setOnClickListener { popupWindow.dismiss() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            imagenSeleccionadaUri = data.data
            findViewById<ImageView>(R.id.ivFotoBarbero)?.apply {
                setImageURI(imagenSeleccionadaUri)
                visibility = View.VISIBLE
            }
        }
    }

    private fun eliminarBarbero(id: Int) {
        viewModel.eliminarBarbero(id)
    }
}
