package com.example.library.data.supabase

data class EventoInsert(
    val nome: String,
    val tipo: String,
    val local: String,
    val data_hora: String,
    val imagem: String? = null
)
