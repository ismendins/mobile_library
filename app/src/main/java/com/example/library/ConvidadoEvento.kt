package com.example.library

import com.google.gson.annotations.SerializedName

data class ConvidadoEvento(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("evento_id") val eventoId: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("created_at") val createdAt: String? = null
)
