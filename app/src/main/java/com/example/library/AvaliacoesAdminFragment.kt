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

class AvaliacoesAdminFragment : Fragment() {

    private lateinit var rvAvaliacoes: RecyclerView
    private lateinit var adapter: AvaliacoesAdminAdapter
    private val supabaseApi = SupabaseClient.api

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_avaliacoes_admin, container, false)
        rvAvaliacoes = view.findViewById(R.id.rv_avaliacoes_admin)
        rvAvaliacoes.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarAvaliacoesPendentes()
    }

    private fun carregarAvaliacoesPendentes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseApi.getAvaliacoesPendentes(
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val avaliacoes = response.body()!!
                    withContext(Dispatchers.Main) {
                        adapter = AvaliacoesAdminAdapter(
                            avaliacoes,
                            ::aprovarAvaliacao,
                            ::rejeitarAvaliacao
                        )
                        rvAvaliacoes.adapter = adapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Erro ao carregar avaliações: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Exceção ao carregar avaliações: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun atualizarStatusAvaliacao(avaliacao: Avaliacao, novoStatus: String) {
        val id = avaliacao.id
        if (id == null) {
            Toast.makeText(context, "Erro: avaliação sem ID.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val statusUpdate = mapOf("status" to novoStatus)
                val response = supabaseApi.atualizarStatusAvaliacao(
                    idFilter = "eq.$id",                      // 👈 aqui o filtro certo
                    statusUpdate = statusUpdate,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Avaliação $id ${novoStatus}a com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        carregarAvaliacoesPendentes()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Erro ao atualizar status da avaliação: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Exceção ao atualizar status da avaliação: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun aprovarAvaliacao(avaliacao: Avaliacao) {
        atualizarStatusAvaliacao(avaliacao, "aprovado")
    }

    private fun rejeitarAvaliacao(avaliacao: Avaliacao) {
        atualizarStatusAvaliacao(avaliacao, "rejeitado")
    }
}
