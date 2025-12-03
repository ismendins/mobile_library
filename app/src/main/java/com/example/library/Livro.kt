package com.example.library

import com.google.gson.annotations.SerializedName

data class Livro(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("autores") val autores: String,
    @SerializedName("idiomas") val idiomas: String,
    @SerializedName("capa_url") val capaUrl: String? = null,
    @SerializedName("criado_em") val criadoEm: String? = null,
    @SerializedName("atualizado_em") val atualizadoEm: String? = null
)
