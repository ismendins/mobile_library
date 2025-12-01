package com.example.library

import com.example.library.data.supabase.Usuario
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable@Serializable
data class AprovacaoEmprestimo(
    val id: Long? = null,
    val usuario_id: Long,
    val livro_id: Long,

    @SerialName("usuarios")
    val usuario: Usuario,

    @SerialName("livros")
    val livro: Book,

    val data_solicitacao: String? = null, // vem como string ISO
    val data_inicio: String,
    val data_fim: String,
    val status: String = "Pendente",
    val observacoes: String? = null
)