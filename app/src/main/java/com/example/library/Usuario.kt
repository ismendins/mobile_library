package com.example.library

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nome_completo") val nomeCompleto: String,
    @SerializedName("email") val email: String,
    @SerializedName("matricula") val matricula: String,
    @SerializedName("senha_hash") val senhaHash: String? = null, // Pode ser nulo se não for usado para retorno
    @SerializedName("data_cadastro") val dataCadastro: String? = null,
    @SerializedName("total_lidos") val totalLidos: Int? = 0,
    @SerializedName("tipo_usuario") val tipoUsuario: String // Ex: "aluno", "bibliotecario"
)
