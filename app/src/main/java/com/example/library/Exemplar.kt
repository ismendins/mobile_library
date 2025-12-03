package com.example.library

import com.google.gson.annotations.SerializedName

data class Exemplar(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("livro_id") val livroId: Int,
    @SerializedName("registro") val registro: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("editora") val editora: String,
    @SerializedName("edicao") val edicao: String,
    @SerializedName("ano") val ano: Int,
    @SerializedName("suporte") val suporte: String,
    @SerializedName("localizacao") val localizacao: String,
    @SerializedName("situacao") val situacao: String,
    @SerializedName("sinopse") val sinopse: String? = null,
    @SerializedName("criado_em") val criadoEm: String? = null,
    @SerializedName("atualizado_em") val atualizadoEm: String? = null
)
