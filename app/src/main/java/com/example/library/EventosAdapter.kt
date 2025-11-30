package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventosAdapter(
private val eventos: List<Evento>,
private val onItemClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventosAdapter.EventoViewHolder>() {

    inner class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tvTituloEventoItem)
        val tipo: TextView = itemView.findViewById(R.id.tvTipoEvento)
        val horario: TextView = itemView.findViewById(R.id.tvHorarioEvento)
        val local: TextView = itemView.findViewById(R.id.tvLocalEvento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eventos_mes, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventos[position]
        holder.titulo.text = evento.titulo
        holder.tipo.text = evento.tipo
        holder.horario.text = evento.horario
        holder.local.text = evento.local

        holder.itemView.setOnClickListener {
            onItemClick(evento)  // Aqui redireciona para DetalhesEvento
        }
    }

    override fun getItemCount() = eventos.size
}
