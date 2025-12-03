package com.example.library

import android.net.Uri
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecomendadosHomeAdapter(
    private val lista: List<Livro>,
    private val onItemClick: (Livro) -> Unit
) : RecyclerView.Adapter<RecomendadosHomeAdapter.RecomendadoViewHolder>() {

    inner class RecomendadoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val capa: ImageView = view.findViewById(R.id.imgCapaHome)

        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(lista[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomendado_home, parent, false)
        return RecomendadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecomendadoViewHolder, position: Int) {
        val livro = lista[position]

        if (!livro.capaUrl.isNullOrEmpty()) {
            Glide.with(holder.capa.context)
                .load(livro.capaUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .into(holder.capa)
        } else {
            holder.capa.setImageResource(R.drawable.ic_book_placeholder)
        }
    }

    override fun getItemCount(): Int = lista.size
}
