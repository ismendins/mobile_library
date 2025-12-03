package com.example.library

import com.google.gson.annotations.SerializedName

data class Emprestimo(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("exemplar_id") val exemplarId: Int, // Assumindo que o empréstimo é de um exemplar específico
    @SerializedName("data_inicio") val dataInicio: String,
    @SerializedName("data_prevista_fim") val dataPrevistaFim: String,
    @SerializedName("data_devolucao") val dataDevolucao: String? = null,
    @SerializedName("status") val status: String // Ex: "pendente", "emprestado", "devolvido"
)
