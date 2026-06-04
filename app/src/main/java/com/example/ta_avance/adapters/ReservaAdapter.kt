package com.example.ta_avance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_avance.R
import com.example.ta_avance.dto.reserva.DtoReserva
import com.google.android.material.button.MaterialButton

class ReservaAdapter(
    private val listaReservas: List<DtoReserva>,
    private val listener: OnReservaClickListener,
    private val estadoActual: String
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    interface OnReservaClickListener {
        fun onVerDetallesClick(reserva: DtoReserva)
        fun onReservaRealizadaClick(reserva: DtoReserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = listaReservas[position]

        holder.tvUsuario.text = "Usuario: ${reserva.usuarioNombre}"
        holder.tvBarbero.text = "Barbero: ${reserva.barberoNombre}"
        holder.tvHorario.text = "Horario: ${reserva.horarioRango}"
        holder.tvServicio.text = "Servicio: ${reserva.servicioNombre}"

        when (estadoActual) {
            "CREADA" -> {
                holder.btnVerDetalles.visibility = View.VISIBLE
                holder.btnEstadoARealizada.visibility = View.GONE
                holder.tvFecha.visibility = View.GONE
                holder.btnVerDetalles.setOnClickListener { listener.onVerDetallesClick(reserva) }
            }
            "CONFIRMADA" -> {
                holder.btnVerDetalles.visibility = View.GONE
                holder.btnEstadoARealizada.visibility = View.VISIBLE
                holder.tvFecha.visibility = View.GONE
                holder.btnEstadoARealizada.setOnClickListener { listener.onReservaRealizadaClick(reserva) }
            }
            "REALIZADA", "CANCELADA" -> {
                holder.btnVerDetalles.visibility = View.GONE
                holder.btnEstadoARealizada.visibility = View.GONE
                holder.tvFecha.visibility = View.VISIBLE
                holder.tvFecha.text = "Fecha: ${reserva.fechaReserva}"
            }
        }
    }

    override fun getItemCount() = listaReservas.size

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsuario: TextView = itemView.findViewById(R.id.tvUsuario)
        val tvBarbero: TextView = itemView.findViewById(R.id.tvBarbero)
        val tvHorario: TextView = itemView.findViewById(R.id.tvHorario)
        val tvServicio: TextView = itemView.findViewById(R.id.tvServicio)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val btnVerDetalles: MaterialButton = itemView.findViewById(R.id.btnVerDetallesReserva)
        val btnEstadoARealizada: MaterialButton = itemView.findViewById(R.id.btnEstadoARealizada)
    }
}