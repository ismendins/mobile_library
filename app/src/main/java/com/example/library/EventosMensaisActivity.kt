package com.example.library


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.emptyList

class EventosMensaisActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageButton
    private lateinit var btnAddEvento: ImageButton
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerEventosMes: RecyclerView
    private lateinit var adapter: EventosAdapter

    private var dataSelecionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mensais)

        // Inicializar views
        btnVoltar = findViewById(R.id.botao_voltar)
        btnAddEvento = findViewById(R.id.botaoAddEvento)
        calendarView = findViewById(R.id.calendarView)
        recyclerEventosMes = findViewById(R.id.recyclerEventosMes)

        // RecyclerView
        recyclerEventosMes.layoutManager = LinearLayoutManager(this)
        adapter = EventosAdapter(emptyList<Evento>())
        adapter = EventosAdapter(emptyList())
        recyclerEventosMes.adapter = adapter

        // Configura ações dos botões
        configurarAcoes()

        // Controla acesso ao botão de adicionar eventos
        val isAdmin = SessionManager.isAdmin(this)
        btnAddEvento.isEnabled = isAdmin
        btnAddEvento.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Data inicial (hoje)
        val hoje = Calendar.getInstance().time
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dataSelecionada = formato.format(hoje)
        carregarEventosDoDia(dataSelecionada)

        // Listener do CalendarView
        calendarView.setOnDateChangeListener { _, ano, mes, dia ->
            val mesFormat = (mes + 1).toString().padStart(2, '0')
            val diaFormat = dia.toString().padStart(2, '0')
            dataSelecionada = "$ano-$mesFormat-$diaFormat"
            carregarEventosDoDia(dataSelecionada)
        }
    }

    private fun configurarAcoes() {
        btnVoltar.setOnClickListener {
            startActivity(Intent(this, MenuInicialActivity::class.java))
        }

        btnAddEvento.setOnClickListener {
            startActivity(Intent(this, FormularioEventosActivity::class.java))
        }
    }

    private fun carregarEventosDoDia(data: String) {
        lifecycleScope.launch {
            try {
                val response = SupabaseClient.api.listarEventos(
                    dataFilter = data,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val lista: List<Evento> = response.body() ?: emptyList()
                    adapter.atualizarLista(lista)
                } else {
                    Toast.makeText(
                        this@EventosMensaisActivity,
                        "Erro ao carregar eventos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@EventosMensaisActivity,
                    "Falha na conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
