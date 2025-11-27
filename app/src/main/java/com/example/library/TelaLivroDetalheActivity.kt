package com.example.library

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import java.util.Calendar

class TelaLivroDetalheActivity : AppCompatActivity() {

    private var tituloLivro: String = "Título do livro"
    private var capaResId: Int = R.drawable.livro1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livro_detalhe)

        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        val btnAlugar = findViewById<Button>(R.id.btnAlugar)
        val btnAvaliarLivro = findViewById<Button>(R.id.btnAvaliarLivro)

        val tvTituloLivro = findViewById<TextView>(R.id.tvTituloLivro)
        val imgCapaLivro = findViewById<ImageView>(R.id.imgCapaLivro)
        val tvQtdExemplares = findViewById<TextView>(R.id.tvQtdExemplares)

        val titulo = intent.getStringExtra("LIVRO_TITULO") ?: "Título do livro"
        val capaResIdIntent = intent.getIntExtra("LIVRO_CAPA_RES_ID", R.drawable.livro1)
        val qtdExemp = intent.getIntExtra("LIVRO_QTD_EXEMPLARES", 0)

        tituloLivro = titulo
        capaResId = capaResIdIntent

        tvTituloLivro.text = titulo
        imgCapaLivro.setImageResource(capaResIdIntent)
        tvQtdExemplares.text = "Quantidade de exemplares: $qtdExemp"

        btnVoltar.setOnClickListener { finish() }

        btnAlugar.setOnClickListener {
            abrirPopupAlugar()
        }

        btnAvaliarLivro.setOnClickListener {
            val intent = Intent(this, AvaliacaoLivroActivity::class.java)
            intent.putExtra("LIVRO_TITULO", tituloLivro)
            startActivity(intent)
        }
    }

    // ---------------- POPUP ATUALIZADO ----------------
    private fun abrirPopupAlugar() {

        val inflater: LayoutInflater = layoutInflater
        val view = inflater.inflate(R.layout.popup_alugar_livro, null)

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

        imgPopupCapa.setImageResource(capaResId)
        tvPopupTitulo.text = tituloLivro
        tvPopupUsuario.text = "Gabriel Gomes"

        // CONFIGURA LIMITES DE DATA
        val calendar = Calendar.getInstance()

        val hoje = calendar.timeInMillis
        dateInicio.minDate = hoje
        dateFim.minDate = hoje

        val limiteMaxGeral = hoje + (30L * 24 * 60 * 60 * 1000)
        dateFim.maxDate = limiteMaxGeral

        // Deixa o início já em hoje e escuta mudanças usando init (compatível com API 24+)
        val anoHoje = calendar.get(Calendar.YEAR)
        val mesHoje = calendar.get(Calendar.MONTH)
        val diaHoje = calendar.get(Calendar.DAY_OF_MONTH)

        dateInicio.init(anoHoje, mesHoje, diaHoje) { _, year, monthOfYear, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, monthOfYear, dayOfMonth)
            val inicioMillis = c.timeInMillis

            // Data final não pode ser antes do início
            dateFim.minDate = inicioMillis

            // Limite de 30 dias a partir da data de início
            val limite = inicioMillis + (30L * 24 * 60 * 60 * 1000)
            dateFim.maxDate = limite
        }

        btnConfirmar.setOnClickListener {

            val anoInicio = dateInicio.year
            val mesInicio = dateInicio.month + 1
            val diaInicio = dateInicio.dayOfMonth

            val anoFim = dateFim.year
            val mesFim = dateFim.month + 1
            val diaFim = dateFim.dayOfMonth

            Toast.makeText(
                this,
                "Alugado!\nDe: $diaInicio/$mesInicio/$anoInicio\nAté: $diaFim/$mesFim/$anoFim",
                Toast.LENGTH_LONG
            ).show()

            dialog.dismiss()
        }

        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
