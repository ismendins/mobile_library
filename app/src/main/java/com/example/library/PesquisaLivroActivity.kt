package com.example.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.launch

class PesquisaLivroActivity : AppCompatActivity() {

    private val supabaseApi = SupabaseClient.api

    private lateinit var recyclerLivros: RecyclerView
    private lateinit var adapter: LivroAdapter
    private val listaLivros = mutableListOf<Livro>()

    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisa_livro)

        val btnVoltar     = findViewById<ImageButton>(R.id.btnVoltar)
        val btnSearch     = findViewById<ImageButton>(R.id.btnSearch)
        val txtPesquisa   = findViewById<EditText>(R.id.txtPesquisa)

        val adminCrudContainer = findViewById<LinearLayout>(R.id.adminCrudContainer)
        val btnAddBook         = findViewById<Button>(R.id.btnAddBook)

        recyclerLivros = findViewById(R.id.recyclerLivros)
        recyclerLivros.layoutManager = LinearLayoutManager(this)

        isAdmin = SessionManager.isAdmin(this)

        adminCrudContainer.visibility = if (isAdmin) View.VISIBLE else View.GONE

        btnAddBook.setOnClickListener {
            if (isAdmin) {
                startActivity(Intent(this, RegistrarLivroActivity::class.java))
            } else {
                Toast.makeText(this, "Acesso permitido somente para administradores.", Toast.LENGTH_SHORT).show()
            }
        }

        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MenuInicialActivity::class.java))
            finish()
        }

        btnSearch.setOnClickListener {
            val texto = txtPesquisa.text.toString().trim()
            if (texto.isNotEmpty()) {
                // Implementar a busca no Supabase aqui
                // Por enquanto, vamos manter a lista completa e filtrar localmente
                // A busca real deve ser feita na função carregarLivros com filtro
                carregarLivros(texto)
            } else {
                carregarLivros()
                Toast.makeText(this, "Digite algo para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        adapter = LivroAdapter(
            listaLivros,
            isAdmin = isAdmin,
            onItemClick = { livro ->
                val intent = Intent(this, TelaLivroDetalheActivity::class.java)
                intent.putExtra("LIVRO_ID", livro.id)
                startActivity(intent)
            },
            onEdit = { livro ->
                if (isAdmin) {
                    val intent = Intent(this, EditarLivroActivity::class.java)
                    intent.putExtra("LIVRO_ID", livro.id)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Somente administradores podem editar livros.", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = { livro ->
                if (isAdmin) {
                    excluirLivro(livro)
                } else {
                    Toast.makeText(this, "Somente administradores podem excluir livros.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerLivros.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        carregarLivros()
    }

    private fun carregarLivros(termoBusca: String? = null) {
        lifecycleScope.launch {
            try {
                val response = if (termoBusca.isNullOrEmpty()) {
                    supabaseApi.getLivros(
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )
                } else {
                    // Busca por título ou autor
                    supabaseApi.searchLivros(
                        termoBusca = "titulo.ilike.*$termoBusca*,autores.ilike.*$termoBusca*",
                        apiKey = SupabaseConfig.SUPABASE_KEY,
                        bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                    )
                }

                if (response.isSuccessful && response.body() != null) {
                    listaLivros.clear()
                    listaLivros.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@PesquisaLivroActivity, "Erro ao carregar livros: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PesquisaLivroActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun excluirLivro(livro: Livro) {
        lifecycleScope.launch {
            try {
                val response = supabaseApi.deletarLivro(
                    idFilter = "eq.${livro.id}",                  // 🔹 nome e tipo certos
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PesquisaLivroActivity,
                        "Livro excluído com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    carregarLivros() // Recarrega a lista
                } else {
                    Toast.makeText(
                        this@PesquisaLivroActivity,
                        "Erro ao excluir livro: ${response.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@PesquisaLivroActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
