package com.example.library

import com.google.gson.annotations.SerializedName

data class AprovacaoEmprestimo(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("livro_id") val livroId: Int,
    @SerializedName("data_solicitacao") val dataSolicitacao: String? = null,
    @SerializedName("data_inicio") val dataInicio: String,
    @SerializedName("data_fim") val dataFim: String,
    @SerializedName("status") val status: String, // Ex: "pendente", "aprovado", "rejeitado"
    @SerializedName("observacoes") val observacoes: String? = null
)
