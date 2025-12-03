package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LivroAdapter(
    private val lista: List<Livro>,
    private val isAdmin: Boolean,
    private val onItemClick: (Livro) -> Unit,
    private val onEdit: (Livro) -> Unit,
    private val onDelete: (Livro) -> Unit
) : RecyclerView.Adapter<LivroAdapter.LivroViewHolder>() {

    inner class LivroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val capa: ImageView = view.findViewById(R.id.imgCapaLivro)
        val titulo: TextView = view.findViewById(R.id.tvTituloLivro)
        val autor: TextView = view.findViewById(R.id.tvAutorLivro)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        init {
            view.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClick(lista[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livro, parent, false)
        return LivroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        val livro = lista[position]

        holder.titulo.text = livro.titulo
        holder.autor.text = livro.autores

        if (!livro.capaUrl.isNullOrEmpty()) {
            Glide.with(holder.capa.context)
                .load(livro.capaUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .into(holder.capa)
        } else {
            holder.capa.setImageResource(R.drawable.ic_book_placeholder)
        }

        if (isAdmin) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE

            holder.btnEdit.setOnClickListener { onEdit(livro) }
            holder.btnDelete.setOnClickListener { onDelete(livro) }
        } else {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = lista.size
}
