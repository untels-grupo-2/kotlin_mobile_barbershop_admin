package com.example.ta_avance.actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.adapters.ValoracionAdapter
import com.example.ta_avance.dto.valoracion.ValoracionDto
import com.example.ta_avance.viewmodel.ListarValoracionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class ListarValoracionActivity : AppCompatActivity() {

    private lateinit var viewModel: ListarValoracionViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listar_valoracion)

        recyclerView = findViewById(R.id.recyclerViewValoraciones)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this).get(ListarValoracionViewModel::class.java)

        viewModel.valoraciones.observe(this) { valoraciones ->
            recyclerView.adapter = ValoracionAdapter(valoraciones) { valoracion ->
                enviarWsp(valoracion)
                responderValoracion(valoracion)
            }
        }

        viewModel.operacionExitosa.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            viewModel.obtenerValoraciones()
        }

        viewModel.error.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.obtenerValoraciones()
    }

    private fun enviarWsp(valoracion: ValoracionDto) {
        val mensaje = """
            Hola *${valoracion.usuario_nombre}*, muchas gracias por tu opinión.
            Nos ayuda a mejorar nuestro servicio.
        """.trimIndent()
        try {
            val uri = "https://wa.me/51${valoracion.celular}?text=${URLEncoder.encode(mensaje, "UTF-8")}"
            startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(uri) })
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun responderValoracion(valoracion: ValoracionDto) {
        viewModel.responderValoracion(valoracion.valoracion_id)
        viewModel.obtenerValoraciones()
    }
}