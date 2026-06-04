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
import com.example.ta_avance.dto.login.LoginRequest

class UsuarioAdapter(
    private val usuarios: List<LoginRequest>,
    private val listener: OnUsuarioClickListener
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    interface OnUsuarioClickListener {
        fun onMessageWsp(usuario: LoginRequest)
        fun onVerReservas(usuario: LoginRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.textNombreCompleto.text = "${usuario.nombre} ${usuario.apellido}"
        holder.textNumero.text = usuario.celular
        holder.textEmail.text = usuario.email
        Glide.with(holder.itemView.context)
            .load(usuario.urlUsuario)
            .placeholder(R.drawable.baseline_person_24)
            .error(R.drawable.baseline_person_24)
            .into(holder.ivFotoUsuario)

        holder.btnEnviarWspUsuarioCreado.setOnClickListener { listener.onMessageWsp(usuario) }
        holder.btnReservaPorUsuario.setOnClickListener { listener.onVerReservas(usuario) }
    }

    override fun getItemCount() = usuarios.size

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNombreCompleto: TextView = itemView.findViewById(R.id.textNombreCompleto)
        val textNumero: TextView = itemView.findViewById(R.id.textNumero)
        val textEmail: TextView = itemView.findViewById(R.id.textEmail)
        val btnEnviarWspUsuarioCreado: Button = itemView.findViewById(R.id.btnEnviarWspUsuarioCreado)
        val btnReservaPorUsuario: Button = itemView.findViewById(R.id.btnReservaPorUsuario)
        val ivFotoUsuario: ImageView = itemView.findViewById(R.id.ivFotoUsuario)
    }
}