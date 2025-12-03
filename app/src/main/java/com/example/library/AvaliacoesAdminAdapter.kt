package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AvaliacoesAdminAdapter(
    private var avaliacoes: List<Avaliacao>,
    private val onAprovar: (Avaliacao) -> Unit,
    private val onRejeitar: (Avaliacao) -> Unit
) : RecyclerView.Adapter<AvaliacoesAdminAdapter.AvaliacaoViewHolder>() {

    class AvaliacaoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tv_avaliacao_usuario)
        val tvLivro: TextView = view.findViewById(R.id.tv_avaliacao_livro)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar_avaliacao)
        val tvComentario: TextView = view.findViewById(R.id.tv_avaliacao_comentario)
        val btnAprovar: Button = view.findViewById(R.id.btn_aprovar_avaliacao)
        val btnRejeitar: Button = view.findViewById(R.id.btn_rejeitar_avaliacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaliacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avaliacao_admin, parent, false)
        return AvaliacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvaliacaoViewHolder, position: Int) {
        val avaliacao = avaliacoes[position]

        // TODO: Substituir por nome de usuário e título de livro reais
        holder.tvUsuario.text = "Usuário ID: ${avaliacao.usuarioId}"
        holder.tvLivro.text = "Livro ID: ${avaliacao.livroId}"
        holder.ratingBar.rating = avaliacao.nota.toFloat()
        holder.tvComentario.text = "Comentário: ${avaliacao.comentario}"

        holder.btnAprovar.setOnClickListener { onAprovar(avaliacao) }
        holder.btnRejeitar.setOnClickListener { onRejeitar(avaliacao) }
    }

    override fun getItemCount() = avaliacoes.size

    fun updateList(novaLista: List<Avaliacao>) {
        avaliacoes = novaLista
        notifyDataSetChanged()
    }
}
