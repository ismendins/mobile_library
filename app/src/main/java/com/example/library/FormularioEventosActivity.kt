package com.example.library

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.library.data.supabase.SupabaseClient
import com.example.library.data.supabase.SupabaseConfig
import com.example.library.data.supabase.EventoInsert
import com.example.library.data.supabase.ConvidadoInsert
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*

class FormularioEventosActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etLocal: EditText
    private lateinit var etDataHora: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var checkboxConvidados: CheckBox
    private lateinit var containerConvidados: LinearLayout
    private lateinit var btnAdicionarConvidado: Button
    private lateinit var layoutAdicionarImagem: LinearLayout
    private lateinit var ivImagemEvento: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var btnCriarEvento: Button

    private var imagemUri: Uri? = null
    private val REQUEST_IMAGEM = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_evento)

        // Inicializar views
        etNome = findViewById(R.id.etNome)
        etLocal = findViewById(R.id.etLocal)
        etDataHora = findViewById(R.id.etDataHora)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        checkboxConvidados = findViewById(R.id.checkboxConvidados)
        containerConvidados = findViewById(R.id.containerConvidados)
        btnAdicionarConvidado = findViewById(R.id.btnAdicionarConvidado)
        layoutAdicionarImagem = findViewById(R.id.layoutAdicionarImagem)
        ivImagemEvento = findViewById(R.id.ivImagemEvento)
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder)
        btnCriarEvento = findViewById(R.id.btnCriarEvento)

        // Spinner de tipos
        val tipos: List<String> = listOf("Palestra", "Seminário", "Roda de Conversa", "Lançamento", "Outro")
        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        etDataHora.setOnClickListener { abrirDateTimePicker() }

        checkboxConvidados.setOnCheckedChangeListener { _, checked ->
            containerConvidados.visibility = if (checked) LinearLayout.VISIBLE else LinearLayout.GONE
            btnAdicionarConvidado.visibility = if (checked) Button.VISIBLE else Button.GONE
        }

        btnAdicionarConvidado.setOnClickListener { adicionarConvidado() }

        layoutAdicionarImagem.setOnClickListener { selecionarImagem() }

        btnCriarEvento.setOnClickListener { salvarEvento() }
    }

    private fun selecionarImagem() {
        val intent = android.content.Intent(android.content.Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGEM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGEM && resultCode == RESULT_OK) {
            imagemUri = data?.data
            ivImagemEvento.setImageURI(imagemUri)
            ivImagemEvento.visibility = ImageView.VISIBLE
            layoutPlaceholder.visibility = LinearLayout.GONE
        }
    }

    private fun abrirDateTimePicker() {
        val calendario = Calendar.getInstance()
        android.app.DatePickerDialog(
            this,
            { _, ano, mes, dia ->
                android.app.TimePickerDialog(
                    this,
                    { _, hora, minuto ->
                        etDataHora.setText(
                            String.format("%02d/%02d/%04d %02d:%02d", dia, mes + 1, ano, hora, minuto)
                        )
                    },
                    calendario.get(Calendar.HOUR_OF_DAY),
                    calendario.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun adicionarConvidado() {
        val template = findViewById<CardView>(R.id.cardConvidadoTemplate)
        val novoCard = LayoutInflater.from(this).inflate(template.id, containerConvidados, false)

        // Atualizar número do convidado
        val tvNumero = novoCard.findViewById<TextView>(R.id.tvConvidadoNumero)
        tvNumero.text = "Convidado ${containerConvidados.childCount + 1}"

        // Botão remover
        val btnRemover = novoCard.findViewById<ImageButton>(R.id.btnRemoverConvidado)
        btnRemover.setOnClickListener {
            containerConvidados.removeView(novoCard)
        }

        containerConvidados.addView(novoCard)
    }

    private fun salvarEvento() {
        val nome = etNome.text.toString().trim()
        val tipo = spinnerTipo.selectedItem.toString()
        val local = etLocal.text.toString().trim()
        val dataHora = etDataHora.text.toString().trim()

        if (nome.isEmpty() || local.isEmpty() || dataHora.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            return
        }

        btnCriarEvento.isEnabled = false

        lifecycleScope.launch {
            try {
                // Upload de imagem (opcional)
                var urlImagem: String? = null
                if (imagemUri != null) {
                    // Aqui você precisa criar uma função no SupabaseApi para upload via Retrofit
                    // ou usar o SDK do Kotlin se disponível
                }

                // Criar objeto evento
                val evento = EventoInsert(nome = nome, tipo = tipo, local = local, data_hora = dataHora, imagem = urlImagem)

                // Inserir evento via Retrofit
                val response = SupabaseClient.api.registrarEvento(
                    novoEvento = evento,
                    apiKey = SupabaseConfig.SUPABASE_KEY,
                    bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                )

                if (response.isSuccessful) {
                    val eventoCriado = response.body()?.firstOrNull()
                    val eventoId = eventoCriado?.id

                    if (checkboxConvidados.isChecked && eventoId != null) {
                        for (i in 0 until containerConvidados.childCount) {
                            val card = containerConvidados.getChildAt(i) as CardView
                            val linear = card.getChildAt(0) as LinearLayout
                            val etNomeConv = linear.findViewById<EditText>(R.id.etNomeConvidado)
                            val etDescConv = linear.findViewById<EditText>(R.id.etDescricaoConvidado)
                            val convidado = ConvidadoInsert(
                                evento_id = eventoId,
                                nome = etNomeConv.text.toString(),
                                descricao = etDescConv.text.toString()
                            )
                            SupabaseClient.api.registrarConvidado(
                                novoConvidado = convidado,
                                apiKey = SupabaseConfig.SUPABASE_KEY,
                                bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                            )
                        }
                    }

                    Toast.makeText(this@FormularioEventosActivity, "Evento criado com sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@FormularioEventosActivity, "Erro ao criar evento: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@FormularioEventosActivity, "Falha: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnCriarEvento.isEnabled = true
            }
        }
    }
}
