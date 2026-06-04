package com.example.ta_avance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ta_avance.R
import com.example.ta_avance.dto.barbero.BarberoDto

class BarberoAdapter(
    private val barberos: List<BarberoDto>,
    private val listener: OnBarberoClickListener
) : RecyclerView.Adapter<BarberoAdapter.BarberoViewHolder>() {

    interface OnBarberoClickListener {
        fun onActualizar(barbero: BarberoDto)
        fun onEliminar(barbero: BarberoDto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberoViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_barbero, parent, false)
        return BarberoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: BarberoViewHolder, position: Int) {
        val barbero = barberos[position]
        holder.textNombre.text = barbero.nombre
        Glide.with(holder.itemView.context)
            .load(barbero.urlBarbero)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .into(holder.ivFotoBarbero)

        holder.btnActualizar.setOnClickListener { listener.onActualizar(barbero) }
        holder.btnEliminar.setOnClickListener { listener.onEliminar(barbero) }
    }

    override fun getItemCount() = barberos.size

    class BarberoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombre: TextView = itemView.findViewById(R.id.textNombre)
        val textEstado: TextView = itemView.findViewById(R.id.textEstado)
        val btnActualizar: Button = itemView.findViewById(R.id.btnActualizar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val ivFotoBarbero: ImageView = itemView.findViewById(R.id.ivFotoBarbero)
    }
}