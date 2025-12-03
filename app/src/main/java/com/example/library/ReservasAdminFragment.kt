package com.example.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReservasAdminFragment : Fragment() {

    private lateinit var rvReservas: RecyclerView
    private lateinit var adapter: ReservasAdminAdapter
    private val supabaseApi = SupabaseClient.api

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservas_admin, container, false)
        rvReservas = view.findViewById(R.id.rv_reservas_admin)
        rvReservas.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarSolicitacoes()
    }

    private fun carregarSolicitacoes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseApi.getSolicitacoesPendentes(
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val solicitacoes = response.body()!!
                    withContext(Dispatchers.Main) {
                        adapter = ReservasAdminAdapter(
                            solicitacoes,
                            ::aprovarReserva,
                            ::rejeitarReserva
                        )
                        rvReservas.adapter = adapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Erro ao carregar solicitações: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Exceção ao carregar solicitações: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun atualizarStatus(solicitacao: AprovacaoEmprestimo, novoStatus: String) {
        val id = solicitacao.id
        if (id == null) {
            Toast.makeText(context, "Erro: solicitação sem ID.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val statusUpdate = mapOf("status" to novoStatus)
                val response = supabaseApi.atualizarStatusSolicitacao(
                    idFilter = "eq.$id",
                    statusUpdate = statusUpdate,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Solicitação $id ${novoStatus}a com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        carregarSolicitacoes()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Erro ao atualizar status: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Exceção ao atualizar status: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun aprovarReserva(solicitacao: AprovacaoEmprestimo) {
        atualizarStatus(solicitacao, "aprovado")
    }

    private fun rejeitarReserva(solicitacao: AprovacaoEmprestimo) {
        atualizarStatus(solicitacao, "rejeitado")
    }
}
