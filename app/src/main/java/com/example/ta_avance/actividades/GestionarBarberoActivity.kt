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
import com.shared.models.dto.barbero.BarberoDto
import com.shared.models.ui.state.UiState
import com.example.ta_avance.viewmodel.GestionarBarberoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GestionarBarberoActivity : AppCompatActivity() {

    private lateinit var viewModel: GestionarBarberoViewModel
    private lateinit var recyclerView: RecyclerView
    private var imagenSeleccionadaUri: Uri? = null

    companion object {
        private const val REQUEST_SELECT_IMAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_barbero)

        recyclerView = findViewById(R.id.recyclerViewBarberos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this).get(GestionarBarberoViewModel::class.java)

        findViewById<Button>(R.id.btnAgregarBarbero).setOnClickListener { mostrarPopupNuevoBarbero(it) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            recyclerView.adapter = BarberoAdapter(state.data, object : BarberoAdapter.OnBarberoClickListener {
                                override fun onActualizar(barbero: BarberoDto) { mostrarPopupActualizarBarbero(barbero) }
                                override fun onEliminar(barbero: BarberoDto) { viewModel.eliminarBarbero(barbero.barbero_id) }
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
                            viewModel.obtenerBarberos()
                        }
                        is UiState.Error -> Toast.makeText(this@GestionarBarberoActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        viewModel.obtenerBarberos()
    }

    private fun mostrarPopupNuevoBarbero(anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_barbero, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        val rootView = window.decorView.rootView as ViewGroup
        val dim = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        rootView.addView(dim, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
        popup.setOnDismissListener { rootView.removeView(dim) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreNuevoBarbero)
        popupView.findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }
        popupView.findViewById<Button>(R.id.btnCrearBarbero).setOnClickListener {
            viewModel.crearBarbero(this, etNombre.text.toString().trim(), imagenSeleccionadaUri)
            imagenSeleccionadaUri = null
            popup.dismiss()
        }
        popupView.findViewById<Button>(R.id.btnCancelar).setOnClickListener { popup.dismiss() }
    }

    private fun mostrarPopupActualizarBarbero(barbero: BarberoDto) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_barbero, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        val rootView = window.decorView.rootView as ViewGroup
        val dim = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        rootView.addView(dim, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup.showAtLocation(recyclerView, Gravity.CENTER, 0, 0)
        popup.setOnDismissListener { rootView.removeView(dim) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreNuevoBarbero).also { it.setText(barbero.nombre) }
        popupView.findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }
        popupView.findViewById<Button>(R.id.btnCrearBarbero).apply {
            text = "Actualizar"
            setOnClickListener {
                viewModel.actualizarBarbero(this@GestionarBarberoActivity, barbero.barbero_id, etNombre.text.toString().trim(), imagenSeleccionadaUri)
                popup.dismiss()
            }
        }
        popupView.findViewById<Button>(R.id.btnCancelar).setOnClickListener { popup.dismiss() }
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
}
