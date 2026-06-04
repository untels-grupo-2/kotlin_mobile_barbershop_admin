package com.example.ta_avance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.dto.valoracion.ValoracionDto

class ValoracionAdapter(
    private val valoraciones: List<ValoracionDto>,
    private val listener: (ValoracionDto) -> Unit
) : RecyclerView.Adapter<ValoracionAdapter.ValoracionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValoracionViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_valoracion, parent, false)
        return ValoracionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ValoracionViewHolder, position: Int) {
        val valoracion = valoraciones[position]
        holder.tvNombreCliente.text = "Usuario: ${valoracion.usuario_nombre}"
        holder.tvValoracion.text = "Valoración: ${valoracion.valoracion}"
        holder.tvMensaje.text = "Mensaje: ${valoracion.mensaje}"
        holder.btnAgradecerValoracion.setOnClickListener { listener(valoracion) }
    }

    override fun getItemCount() = valoraciones.size

    class ValoracionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        val tvValoracion: TextView = itemView.findViewById(R.id.tvValoracion)
        val tvMensaje: TextView = itemView.findViewById(R.id.tvMensaje)
        val btnAgradecerValoracion: Button = itemView.findViewById(R.id.btnAgradecerValoracion)
    }
}