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
import com.example.ta_avance.dto.servicio.ServicioDto

class ServicioAdapter(
    private val servicios: List<ServicioDto>,
    private val listener: OnServicioClickListener
) : RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>() {

    interface OnServicioClickListener {
        fun onActualizar(servicio: ServicioDto)
        fun onEliminar(servicio: ServicioDto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServicioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.textNombre.text = servicio.nombre
        holder.textPrecio.text = "S/ ${servicio.precio}"
        holder.textDescripcion.text = servicio.descripcion
        holder.textTipoServicio.text = servicio.nombre_tipoServicio
        Glide.with(holder.itemView.context)
            .load(servicio.urlServicio)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .into(holder.ivFotoServicio)

        holder.btnActualizar.setOnClickListener { listener.onActualizar(servicio) }
        holder.btnEliminar.setOnClickListener { listener.onEliminar(servicio) }
    }

    override fun getItemCount() = servicios.size

    class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombre: TextView = itemView.findViewById(R.id.textNombreServicio)
        val textPrecio: TextView = itemView.findViewById(R.id.textPrecioServicio)
        val textDescripcion: TextView = itemView.findViewById(R.id.textDescripcionServicio)
        val textTipoServicio: TextView = itemView.findViewById(R.id.textTipoServicio)
        val btnActualizar: Button = itemView.findViewById(R.id.btnActualizarServicio)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarServicio)
        val ivFotoServicio: ImageView = itemView.findViewById(R.id.ivFotoServicio)
    }
}