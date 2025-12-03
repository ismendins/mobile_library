package com.example.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ReservasAdminAdapter(
    private var solicitacoes: List<AprovacaoEmprestimo>,
    private val onAprovar: (AprovacaoEmprestimo) -> Unit,
    private val onRejeitar: (AprovacaoEmprestimo) -> Unit
) : RecyclerView.Adapter<ReservasAdminAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuarioNome: TextView = view.findViewById(R.id.tv_usuario_nome)
        val tvLivroTitulo: TextView = view.findViewById(R.id.tv_livro_titulo)
        val tvPeriodo: TextView = view.findViewById(R.id.tv_periodo)
        val btnAprovar: Button = view.findViewById(R.id.btn_aprovar)
        val btnRejeitar: Button = view.findViewById(R.id.btn_rejeitar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva_admin, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val solicitacao = solicitacoes[position]

        // Assumindo que a query do Supabase retorna os dados do usuário e livro aninhados
        // Se não retornar, precisaremos de um endpoint separado ou um modelo de dados mais complexo
        // Por enquanto, usaremos placeholders/dados simulados
        val usuarioNome = "Usuário ID: ${solicitacao.usuarioId}" // TODO: Substituir por nome real
        val livroTitulo = "Livro ID: ${solicitacao.livroId}" // TODO: Substituir por título real

        holder.tvUsuarioNome.text = "Usuário: $usuarioNome"
        holder.tvLivroTitulo.text = "Livro: $livroTitulo"

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataInicio = try { dateFormat.format(solicitacao.dataInicio) } catch (e: Exception) { solicitacao.dataInicio }
        val dataFim = try { dateFormat.format(solicitacao.dataFim) } catch (e: Exception) { solicitacao.dataFim }

        holder.tvPeriodo.text = "Período: $dataInicio a $dataFim"

        holder.btnAprovar.setOnClickListener { onAprovar(solicitacao) }
        holder.btnRejeitar.setOnClickListener { onRejeitar(solicitacao) }
    }

    override fun getItemCount() = solicitacoes.size

    fun updateList(novaLista: List<AprovacaoEmprestimo>) {
        solicitacoes = novaLista
        notifyDataSetChanged()
    }
}
