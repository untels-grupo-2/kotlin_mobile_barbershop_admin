package com.example.ta_avance.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.ServicioAdapter
import com.shared.models.dto.servicio.ServicioDto
import com.shared.models.ui.state.UiState
import com.example.ta_avance.viewmodel.GestionarServicioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GestionarServicioActivity : AppCompatActivity() {

    private lateinit var viewModel: GestionarServicioViewModel
    private lateinit var recyclerView: RecyclerView
    private var imagenSeleccionadaUri: Uri? = null

    companion object {
        private const val REQUEST_SELECT_IMAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_servicio)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this).get(GestionarServicioViewModel::class.java)

        findViewById<Button>(R.id.btnAgregarServicio).setOnClickListener { mostrarPopupNuevoServicio(it) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            recyclerView.adapter = ServicioAdapter(state.data, object : ServicioAdapter.OnServicioClickListener {
                                override fun onActualizar(servicio: ServicioDto) { mostrarPopupActualizarServicio(servicio) }
                                override fun onEliminar(servicio: ServicioDto) { viewModel.eliminarServicio(servicio.servicio_id) }
                            })
                        }
                        is UiState.Error -> Toast.makeText(this@GestionarServicioActivity, state.message, Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@GestionarServicioActivity, state.data, Toast.LENGTH_SHORT).show()
                            viewModel.obtenerServicios()
                        }
                        is UiState.Error -> Toast.makeText(this@GestionarServicioActivity, state.message, Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
            }
        }

        viewModel.obtenerServicios()
    }

    private fun crearSpinnerAdapter() = ArrayAdapter(
        this, android.R.layout.simple_spinner_item, viewModel.tiposServicio
    ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

    private fun crearDim(): View {
        val dim = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        (window.decorView.rootView as ViewGroup).addView(dim, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return dim
    }

    private fun mostrarPopupNuevoServicio(anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_servicio, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        val rootView = window.decorView.rootView as ViewGroup
        val dim = crearDim()
        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
        popup.setOnDismissListener { rootView.removeView(dim) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreServicio)
        val etPrecio = popupView.findViewById<EditText>(R.id.etPrecioServicio)
        val etDescripcion = popupView.findViewById<EditText>(R.id.etDescripcionServicio)
        val spinner = popupView.findViewById<Spinner>(R.id.spinnerTipoServicio).also { it.adapter = crearSpinnerAdapter() }

        popupView.findViewById<Button>(R.id.btnSeleccionarImagenServicio).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }
        popupView.findViewById<Button>(R.id.btnCrearServicio).setOnClickListener {
            viewModel.crearServicio(
                this,
                etNombre.text.toString().trim(),
                etPrecio.text.toString().trim(),
                etDescripcion.text.toString().trim(),
                spinner.selectedItem as String,
                imagenSeleccionadaUri
            )
            imagenSeleccionadaUri = null
            popup.dismiss()
        }
        popupView.findViewById<Button>(R.id.btnCancelarServicio).setOnClickListener { popup.dismiss() }
    }

    private fun mostrarPopupActualizarServicio(servicio: ServicioDto) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_servicio, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        val rootView = window.decorView.rootView as ViewGroup
        val dim = crearDim()
        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popup.showAtLocation(recyclerView, Gravity.CENTER, 0, 0)
        popup.setOnDismissListener { rootView.removeView(dim) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreServicio).also { it.setText(servicio.nombre) }
        val etPrecio = popupView.findViewById<EditText>(R.id.etPrecioServicio).also { it.setText(servicio.precio.toString()) }
        val etDescripcion = popupView.findViewById<EditText>(R.id.etDescripcionServicio).also { it.setText(servicio.descripcion) }
        val spinner = popupView.findViewById<Spinner>(R.id.spinnerTipoServicio).also {
            it.adapter = crearSpinnerAdapter()
            it.setSelection(viewModel.obtenerIndicesTipoServicio(servicio.tipoServicio_id))
        }

        popupView.findViewById<Button>(R.id.btnSeleccionarImagenServicio).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }
        popupView.findViewById<Button>(R.id.btnCrearServicio).apply {
            text = "Actualizar"
            setOnClickListener {
                viewModel.actualizarServicio(
                    this@GestionarServicioActivity,
                    servicio.servicio_id,
                    etNombre.text.toString().trim(),
                    etPrecio.text.toString().trim(),
                    etDescripcion.text.toString().trim(),
                    spinner.selectedItem as String,
                    imagenSeleccionadaUri
                )
                popup.dismiss()
            }
        }
        popupView.findViewById<Button>(R.id.btnCancelarServicio).setOnClickListener { popup.dismiss() }
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
