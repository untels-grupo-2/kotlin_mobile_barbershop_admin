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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.ServicioAdapter
import com.example.ta_avance.dto.servicio.ServicioDto
import com.example.ta_avance.dto.servicio.ServicioRequest
import com.example.ta_avance.viewmodel.GestionarServicioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GestionarServicioActivity : AppCompatActivity() {

    private lateinit var viewModel: GestionarServicioViewModel
    private lateinit var recyclerView: RecyclerView
    private var imagenSeleccionadaUri: Uri? = null

    private val tipoServicioMap = linkedMapOf(
        "CORTES" to 1,
        "SKINCARE" to 2,
        "AFEITADO DE BARBA" to 3,
        "COLORACIÓN" to 4
    )

    companion object {
        private const val REQUEST_SELECT_IMAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_servicio)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this).get(GestionarServicioViewModel::class.java)

        findViewById<Button>(R.id.btnAgregarServicio).setOnClickListener {
            mostrarPopupNuevoServicio(it)
        }

        viewModel.servicios.observe(this) { servicios ->
            recyclerView.adapter = ServicioAdapter(servicios, object : ServicioAdapter.OnServicioClickListener {
                override fun onActualizar(servicio: ServicioDto) {
                    mostrarPopupActualizarServicio(servicio)
                }
                override fun onEliminar(servicio: ServicioDto) {
                    eliminarServicio(servicio.servicio_id)
                }
            })
        }

        viewModel.operacionExitosa.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.error.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        cargarLista()
    }

    private fun cargarLista() {
        viewModel.obtenerServicios()
    }

    private fun crearSpinnerAdapter() = ArrayAdapter(
        this,
        android.R.layout.simple_spinner_item,
        tipoServicioMap.keys.toTypedArray()
    ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

    private fun crearDimBehind(): View {
        val rootView = window.decorView.rootView as ViewGroup
        val dimBehind = View(this).apply { setBackgroundColor(0x88000000.toInt()) }
        rootView.addView(dimBehind, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return dimBehind
    }

    private fun mostrarPopupNuevoServicio(anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_servicio, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        val rootView = window.decorView.rootView as ViewGroup
        val dimBehind = crearDimBehind()

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener { rootView.removeView(dimBehind) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreServicio)
        val etPrecio = popupView.findViewById<EditText>(R.id.etPrecioServicio)
        val etDescripcion = popupView.findViewById<EditText>(R.id.etDescripcionServicio)
        val spinnerTipo = popupView.findViewById<Spinner>(R.id.spinnerTipoServicio)
        spinnerTipo.adapter = crearSpinnerAdapter()

        popupView.findViewById<Button>(R.id.btnSeleccionarImagenServicio).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }

        popupView.findViewById<Button>(R.id.btnCrearServicio).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val precioStr = etPrecio.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val tipoSeleccionado = spinnerTipo.selectedItem as String
            if (nombre.isNotEmpty() && precioStr.isNotEmpty() && descripcion.isNotEmpty()) {
                val request = ServicioRequest(nombre, precioStr.toDouble(), descripcion, tipoServicioMap[tipoSeleccionado]!!)
                viewModel.crearServicio(this, request, imagenSeleccionadaUri)
                imagenSeleccionadaUri = null
                popupWindow.dismiss()
                cargarLista()
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        popupView.findViewById<Button>(R.id.btnCancelarServicio).setOnClickListener { popupWindow.dismiss() }
    }

    private fun mostrarPopupActualizarServicio(servicio: ServicioDto) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_nuevo_servicio, null)
        popupView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        val rootView = window.decorView.rootView as ViewGroup
        val dimBehind = crearDimBehind()

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener { rootView.removeView(dimBehind) }

        val etNombre = popupView.findViewById<EditText>(R.id.etNombreServicio)
        val etPrecio = popupView.findViewById<EditText>(R.id.etPrecioServicio)
        val etDescripcion = popupView.findViewById<EditText>(R.id.etDescripcionServicio)
        val spinnerTipo = popupView.findViewById<Spinner>(R.id.spinnerTipoServicio)
        spinnerTipo.adapter = crearSpinnerAdapter()

        etNombre.setText(servicio.nombre)
        etPrecio.setText(servicio.precio.toString())
        etDescripcion.setText(servicio.descripcion)

        tipoServicioMap.values.toList().indexOfFirst { it == servicio.tipoServicio_id }
            .takeIf { it >= 0 }?.let { spinnerTipo.setSelection(it) }

        popupView.findViewById<Button>(R.id.btnSeleccionarImagenServicio).setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_SELECT_IMAGE)
        }

        popupView.findViewById<Button>(R.id.btnCrearServicio).apply {
            text = "Actualizar"
            setOnClickListener {
                val nombre = etNombre.text.toString().trim()
                val precioStr = etPrecio.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()
                val tipoSeleccionado = spinnerTipo.selectedItem as String
                if (nombre.isNotEmpty() && precioStr.isNotEmpty() && descripcion.isNotEmpty()) {
                    val request = ServicioRequest(nombre, precioStr.toDouble(), descripcion, tipoServicioMap[tipoSeleccionado]!!)
                    viewModel.actualizarServicio(this@GestionarServicioActivity, servicio.servicio_id, request, imagenSeleccionadaUri)
                    popupWindow.dismiss()
                    cargarLista()
                } else {
                    Toast.makeText(this@GestionarServicioActivity, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        popupView.findViewById<Button>(R.id.btnCancelarServicio).setOnClickListener { popupWindow.dismiss() }
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

    private fun eliminarServicio(id: Int) {
        viewModel.eliminarServicio(id)
        cargarLista()
    }
}