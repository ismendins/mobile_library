package com.example.library

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AprovacoesActivity : AppCompatActivity() {

    private lateinit var recyclerAprovacoes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprovacoes_aluguel)

        recyclerAprovacoes = findViewById(R.id.recyclerAprovacoes)

        val listaReservas = listOf(
            "Gabriel Gomes está solicitando a reserva do livro “A Culpa é das Estrelas” para o período de 26 a 29 de setembro.",
            "Mariana Silva está solicitando a reserva do livro “Dom Casmurro” para o período de 3 a 7 de outubro.",
            "Ricardo Almeida está solicitando a reserva do livro “O Pequeno Príncipe” para o período de 10 a 14 de novembro."
        )

        val adapter = AprovacoesAdapter(listaReservas) { reserva ->
            val intent = Intent(this, TelaLivroDetalheActivity::class.java)
            intent.putExtra("reserva", reserva)
            startActivity(intent)
        }

        recyclerAprovacoes.layoutManager = LinearLayoutManager(this)
        recyclerAprovacoes.adapter = adapter
    }
}
