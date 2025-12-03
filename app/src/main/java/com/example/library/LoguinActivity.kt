package com.example.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.library.SupabaseClient
import com.example.library.SupabaseConfig
import kotlinx.coroutines.launch

class LoguinActivity : AppCompatActivity() {

    companion object {

    }

    private lateinit var btnEntrar: Button
    private lateinit var linkRegistrar: TextView
    private lateinit var inputMatricula: EditText
    private lateinit var inputSenha: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loguin)

        btnEntrar = findViewById(R.id.btnEntrar)
        linkRegistrar = findViewById(R.id.linkRegistrar)
        inputMatricula = findViewById(R.id.inputMatricula)
        inputSenha = findViewById(R.id.inputSenha)

        btnEntrar.setOnClickListener {

            try {
                val matricula = inputMatricula.text.toString().trim()
                val senha     = inputSenha.text.toString().trim()

                if (matricula.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val senhaHash = senha

                lifecycleScope.launch {
                    try {
                        btnEntrar.isEnabled = false

                        val response = SupabaseClient.api.loginUsuario(
                            matriculaFilter = "eq.$matricula",
                            senhaFilter = "eq.$senhaHash",
                            apiKey = SupabaseConfig.SUPABASE_KEY,
                            bearer = "Bearer ${SupabaseConfig.SUPABASE_KEY}"
                        )

                        btnEntrar.isEnabled = true

                        if (!response.isSuccessful) {
                            Toast.makeText(
                                this@LoguinActivity,
                                "Erro ao conectar no servidor (${response.code()})",
                                Toast.LENGTH_LONG
                            ).show()
                            return@launch
                        }

                        val usuarios = response.body() ?: emptyList()
                        // O modelo Usuario retornado pela API precisa ser o novo modelo, mas a função loginUsuario
                        // está retornando o modelo antigo. Vamos assumir que o modelo antigo ainda está em uso
                        // para evitar quebrar o código existente, mas vamos ajustar o SessionManager.

                        if (usuarios.isEmpty()) {
                            Toast.makeText(
                                this@LoguinActivity,
                                "Matrícula ou senha inválidos",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        val usuario = usuarios[0]

                        val isAdmin = usuario.tipoUsuario.uppercase() == "BIBLIOTECARIO" || usuario.tipoUsuario.uppercase() == "ADMIN"

                        SessionManager.saveUser(this@LoguinActivity, usuario.id!!, usuario.nomeCompleto, isAdmin)


                        startActivity(
                            Intent(this@LoguinActivity, MenuInicialActivity::class.java)
                        )
                        finish()

                    } catch (e: Exception) {
                        btnEntrar.isEnabled = true
                        Log.e("LoguinActivity", "Erro na chamada Supabase", e)
                        Toast.makeText(
                            this@LoguinActivity,
                            "Erro ao fazer login: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                    }
                }

            } catch (e: Exception) {
                Log.e("LoguinActivity", "Erro ao abrir a próxima tela", e)
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        linkRegistrar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
