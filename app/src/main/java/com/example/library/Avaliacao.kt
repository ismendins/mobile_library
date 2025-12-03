package com.example.library

import com.google.gson.annotations.SerializedName

data class Avaliacao(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("livro_id") val livroId: Int,
    @SerializedName("nota") val nota: Int,
    @SerializedName("comentario") val comentario: String,
    @SerializedName("status") val status: String = "pendente", // Adicionado para moderação
    @SerializedName("created_at") val createdAt: String? = null
)
