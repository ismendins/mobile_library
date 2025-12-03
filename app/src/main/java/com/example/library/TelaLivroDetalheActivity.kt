package com.example.library

import android.os.Bundle
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TelaLivroDetalheActivity : AppCompatActivity() {

    private lateinit var livroAtual: Livro
    private var livroId: Int = -1
    private lateinit var btnAlugar: Button
    private lateinit var btnEditarLivro: ImageButton
    private lateinit var btnExcluirLivro: ImageButton

    private lateinit var layoutAvaliacoes: LinearLayout
    private lateinit var tvResumoAvaliacoes: TextView
    private lateinit var tvDisponibilidade: TextView

    private val supabaseApi = SupabaseClient.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livro_detalhe)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        btnVoltar.setOnClickListener { finish() }

        btnEditarLivro = findViewById(R.id.btnEditarLivro)
        btnExcluirLivro = findViewById(R.id.btnExcluirLivro)

        if (SessionManager.isAdmin(this)) {
            btnEditarLivro.visibility = View.VISIBLE
            btnExcluirLivro.visibility = View.VISIBLE

            btnEditarLivro.setOnClickListener {
                val intent = Intent(this, EditarLivroActivity::class.java)
                intent.putExtra("LIVRO_ID", livroId)
                startActivity(intent)
            }

            btnExcluirLivro.setOnClickListener { confirmarExclusao() }
        } else {
            btnEditarLivro.visibility = View.GONE
            btnExcluirLivro.visibility = View.GONE
        }

        layoutAvaliacoes = findViewById(R.id.layoutAvaliacoes)
        tvResumoAvaliacoes = findViewById(R.id.tvResumoAvaliacoes)
        tvDisponibilidade = findViewById(R.id.tvDisponibilidade)

        livroId = intent.getIntExtra("LIVRO_ID", -1)

        if (livroId != -1) {
            carregarDetalhesLivro(livroId)
        } else {
            Toast.makeText(this, "Erro: ID do livro não fornecido.", Toast.LENGTH_LONG).show()
            finish()
        }

        val btnAvaliar = findViewById<Button>(R.id.btnAvaliarLivro)
        btnAvaliar.setOnClickListener {
            val intent = Intent(this, AvaliacaoLivroActivity::class.java)
            intent.putExtra("LIVRO_ID", livroId)
            intent.putExtra("LIVRO_TITULO", livroAtual.titulo)
            startActivity(intent)
        }

        btnAlugar = findViewById(R.id.btnAlugar)
        btnAlugar.setOnClickListener { abrirPopupAlugar() }
    }

    private fun carregarDetalhesLivro(id: Int) {
        lifecycleScope.launch {
            try {
                val response = supabaseApi.getLivroPorId(
                    idFilter = "eq.$id",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                val lista = response.body()

                if (response.isSuccessful && lista != null && lista.isNotEmpty()) {
                    livroAtual = lista[0]

                    val tvTitulo = findViewById<TextView>(R.id.tvTituloLivro)
                    val tvAutor = findViewById<TextView>(R.id.tvAutor)
                    val tvIdioma = findViewById<TextView>(R.id.tvIdioma)
                    val imgCapa = findViewById<ImageView>(R.id.imgCapaLivro)

                    tvTitulo.text = livroAtual.titulo
                    tvAutor.text = "Autor(es): ${livroAtual.autores}"
                    tvIdioma.text = "Idioma: ${livroAtual.idiomas}"

                    if (!livroAtual.capaUrl.isNullOrEmpty()) {
                        Glide.with(this@TelaLivroDetalheActivity)
                            .load(livroAtual.capaUrl)
                            .placeholder(R.drawable.ic_book_placeholder)
                            .into(imgCapa)
                    } else {
                        imgCapa.setImageResource(R.drawable.ic_book_placeholder)
                    }

                    carregarExemplares(livroAtual.id!!)
                } else {
                    Toast.makeText(
                        this@TelaLivroDetalheActivity,
                        "Erro ao carregar livro: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@TelaLivroDetalheActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun confirmarExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Livro")
            .setMessage(
                "Tem certeza que deseja excluir o livro \"${livroAtual.titulo}\"? " +
                        "Esta ação é irreversível."
            )
            .setPositiveButton("Excluir") { _, _ -> excluirLivro() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirLivro() {
        lifecycleScope.launch {
            try {
                val response = supabaseApi.deletarLivro(
                    idFilter = "eq.$livroId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@TelaLivroDetalheActivity,
                        "Livro excluído com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@TelaLivroDetalheActivity,
                        "Erro ao excluir livro: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@TelaLivroDetalheActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun carregarExemplares(livroId: Int) {
        lifecycleScope.launch {
            try {
                val response = supabaseApi.getExemplaresPorLivro(
                    livroIdFilter = "eq.$livroId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val exemplaresDisponiveis =
                        response.body()!!.filter { it.situacao == "disponivel" }

                    if (exemplaresDisponiveis.isNotEmpty()) {
                        tvDisponibilidade.text =
                            "Disponível (${exemplaresDisponiveis.size} exemplares)"
                        btnAlugar.isEnabled = true
                    } else {
                        tvDisponibilidade.text = "Indisponível no momento"
                        btnAlugar.isEnabled = false
                    }
                } else {
                    tvDisponibilidade.text = "Indisponível (Erro de conexão)"
                    btnAlugar.isEnabled = false
                }
            } catch (e: Exception) {
                tvDisponibilidade.text = "Indisponível (Erro de conexão)"
                btnAlugar.isEnabled = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAvaliacoes()
    }

    private fun carregarAvaliacoes() {
        if (livroId == -1) return

        layoutAvaliacoes.removeAllViews()

        lifecycleScope.launch {
            try {
                val response = supabaseApi.getAvaliacoesPorLivro(
                    livroIdFilter = "eq.$livroId",
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful && response.body() != null) {
                    val lista =
                        response.body()!!.filter { it.status == "aprovado" }

                    if (lista.isEmpty()) {
                        tvResumoAvaliacoes.text = "0.0 ★ (0 avaliações)"
                        val vazio = TextView(this@TelaLivroDetalheActivity)
                        vazio.text = "Nenhuma avaliação ainda."
                        layoutAvaliacoes.addView(vazio)
                        return@launch
                    }

                    var soma = 0.0
                    for (av in lista) {
                        soma += av.nota

                        val item = layoutInflater.inflate(
                            R.layout.item_avaliacao,
                            layoutAvaliacoes,
                            false
                        )

                        val tvNome = item.findViewById<TextView>(R.id.tvNomeUser)
                        val tvEstrelas = item.findViewById<TextView>(R.id.tvEstrelasUser)
                        val tvComentario = item.findViewById<TextView>(R.id.tvComentarioUser)

                        tvNome.text = "Usuário ID: ${av.usuarioId}"
                        tvEstrelas.text =
                            "★".repeat(av.nota) + "☆".repeat(5 - av.nota)
                        tvComentario.text = av.comentario

                        layoutAvaliacoes.addView(item)
                    }

                    val media = soma / lista.size
                    tvResumoAvaliacoes.text =
                        String.format("%.1f ★ (%d avaliações)", media, lista.size)
                } else {
                    Toast.makeText(
                        this@TelaLivroDetalheActivity,
                        "Erro ao carregar avaliações.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@TelaLivroDetalheActivity,
                    "Erro de conexão ao carregar avaliações: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun abrirPopupAlugar() {
        val view = layoutInflater.inflate(R.layout.popup_alugar_livro, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgPopupCapa = view.findViewById<ImageView>(R.id.imgPopupCapa)
        val tvPopupTitulo = view.findViewById<TextView>(R.id.tvPopupTitulo)
        val tvPopupUsuario = view.findViewById<TextView>(R.id.tvPopupUsuario)
        val dateInicio = view.findViewById<DatePicker>(R.id.dateInicio)
        val dateFim = view.findViewById<DatePicker>(R.id.dateFim)
        val btnConfirmar = view.findViewById<Button>(R.id.btnPopupConfirmar)
        val btnFechar = view.findViewById<TextView>(R.id.btnPopupFechar)

        if (!livroAtual.capaUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(livroAtual.capaUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .into(imgPopupCapa)
        } else {
            imgPopupCapa.setImageResource(R.drawable.ic_book_placeholder)
        }

        tvPopupTitulo.text = livroAtual.titulo
        tvPopupUsuario.text = SessionManager.getUserName(this)

        val calendar = Calendar.getInstance()
        val hoje = calendar.timeInMillis
        dateInicio.minDate = hoje
        dateFim.minDate = hoje

        val limiteMaxGeral = hoje + (30L * 24 * 60 * 60 * 1000)
        dateFim.maxDate = limiteMaxGeral

        btnConfirmar.setOnClickListener {
            val inicio = Calendar.getInstance().apply {
                set(dateInicio.year, dateInicio.month, dateInicio.dayOfMonth)
            }.time
            val fim = Calendar.getInstance().apply {
                set(dateFim.year, dateFim.month, dateFim.dayOfMonth)
            }.time

            if (fim.before(inicio)) {
                Toast.makeText(
                    this,
                    "A data final deve ser depois da data inicial.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val sdf =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val dataInicioStr = sdf.format(inicio)
            val dataFimStr = sdf.format(fim)

            val usuarioId = SessionManager.getUserId(this)
            if (usuarioId == -1) {
                Toast.makeText(
                    this,
                    "Erro: Usuário não logado.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val solicitacao = AprovacaoEmprestimo(
                usuarioId = usuarioId,
                livroId = livroId,
                dataInicio = dataInicioStr,
                dataFim = dataFimStr,
                status = "pendente"
            )

            lifecycleScope.launch {
                try {
                    btnConfirmar.isEnabled = false
                    val response = supabaseApi.solicitarEmprestimo(
                        solicitacao,
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@TelaLivroDetalheActivity,
                            "Solicitação de empréstimo enviada para aprovação!",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@TelaLivroDetalheActivity,
                            "Erro ao solicitar empréstimo: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@TelaLivroDetalheActivity,
                        "Erro de conexão: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnConfirmar.isEnabled = true
                }
            }
        }

        btnFechar.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
