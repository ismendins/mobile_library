package com.example.library;

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventosMensaisActivity : AppCompatActivity() {

    private lateinit var recyclerEventos: RecyclerView
    private lateinit var adapter: EventosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mensais)

        recyclerEventos = findViewById(R.id.recyclerEventosMes)
        recyclerEventos.layoutManager = LinearLayoutManager(this)

    }

//    private fun fetchEventos() {
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = supabaseClient
//                    .from("eventos")
//                    .select("*")
//                    .execute()
//
//                if (response.isSuccessful) {
//                    val eventos: List<Evento> = response.body()!!
//                    withContext(Dispatchers.Main) {
//                        adapter = EventosAdapter(eventos)
//                        recyclerEventos.adapter = adapter
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("EventosActivity", "Erro ao buscar eventos: ${e.message}")
//            }
//        }
//    }
}

