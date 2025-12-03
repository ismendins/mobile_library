package com.example.library

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AvaliacaoLivroActivity : AppCompatActivity() {

    private lateinit var tvTituloAvaliacao: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var edtComentario: EditText
    private lateinit var btnEnviarAvaliacao: Button
    private lateinit var btnVoltar: ImageButton
    private val supabaseApi = SupabaseClient.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliacao_livro)

        tvTituloAvaliacao = findViewById(R.id.tvTituloAvaliacao)
        ratingBar = findViewById(R.id.ratingBar)
        edtComentario = findViewById(R.id.edtComentario)
        btnEnviarAvaliacao = findViewById(R.id.btnEnviarAvaliacao)
        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener { finish() }

        val livroId = intent.getIntExtra("LIVRO_ID", -1)
        val titulo = intent.getStringExtra("LIVRO_TITULO") ?: ""

        tvTituloAvaliacao.text =
            if (titulo.isNotEmpty()) "Avaliar: $titulo" else "Livro não encontrado"

        if (livroId == -1) {
            Toast.makeText(this, "Erro: ID do livro não encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        btnEnviarAvaliacao.setOnClickListener {
            val nota = ratingBar.rating.toInt()
            val comentario = edtComentario.text.toString().trim()

            if (comentario.isEmpty() || nota == 0) {
                Toast.makeText(
                    this,
                    "Por favor, adicione uma nota e um comentário.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val usuarioId = SessionManager.getUserId(this)
            if (usuarioId == -1) {
                Toast.makeText(this, "Erro: Usuário não logado.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val novaAvaliacao = Avaliacao(
                usuarioId = usuarioId,
                livroId = livroId,
                nota = nota,
                comentario = comentario,
                status = "pendente"
            )

            lifecycleScope.launch {
                try {
                    btnEnviarAvaliacao.isEnabled = false
                    val response = supabaseApi.registrarAvaliacao(
                        novaAvaliacao,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AvaliacaoLivroActivity,
                            "Avaliação enviada para moderação!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AvaliacaoLivroActivity,
                            "Erro ao enviar avaliação: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AvaliacaoLivroActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnEnviarAvaliacao.isEnabled = true
                }
            }
        }
    }
}
