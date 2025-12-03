    package com.example.library.data.supabase

import com.example.library.AprovacaoEmprestimo
import com.example.library.Convidado
import com.example.library.Evento
import com.example.library.EventoUpdate
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

    interface SupabaseApi {

        @GET("usuarios")
        suspend fun loginUsuario(
            @Query("matricula") matriculaFilter: String,
            @Query("senha_hash") senhaFilter: String,
            @Query("select") select: String = "id,nome_completo,email,matricula,tipo_usuario",
            @Query("limit") limit: Int = 1,

            @Header("apikey") apiKey: String,
            @Header("Authorization") bearer: String
        ): Response<List<Usuario>>


        @GET("usuarios")
        suspend fun login(
            @Query("select")
            select: String = "id,nome_completo,email,matricula,total_lidos,tipo_usuario",

            @Query("matricula")
            matriculaFilter: String,

            @Query("senha_hash")
            senhaHashFilter: String,

            @Query("limit")
            limit: Int = 1,

            @Header("apikey")
            apiKey: String,

            @Header("Authorization")
            bearer: String
        ): Response<List<Usuario>>

        @Headers("Prefer: return=representation")


        @POST("usuarios")
        suspend fun registrar(
            @Body novoUsuario: UsuarioInsert,

            @Header("apikey")
            apiKey: String,

            @Header("Authorization")
            bearer: String
        ): Response<List<Usuario>>

    @Headers("Prefer: return=representation")
    @POST("eventos")
    suspend fun registrarEvento(
        @Body novoEvento: EventoInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @Headers("Prefer: return=representation")
    @POST("convidados")
    suspend fun registrarConvidado(
        @Body novoConvidado: ConvidadoInsert,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Convidado>>

    @GET("eventos")
    suspend fun listarEventos(
        @Query("data_hora::text") dataFiltro: String,
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

data class RpcDataFiltro(val data_filtro: String)

    @POST("rpc/buscar_eventos_por_data")
    suspend fun buscarEventosPorData(
        @Body body: RpcDataFiltro,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @DELETE("eventos")
    suspend fun deletarEvento(
        @Query("id") idFiltro: String,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @Headers("Prefer: return=representation")
    @PATCH("eventos")
    suspend fun atualizarEvento(
        @Query("id") idFiltro: String,
        @Body body: EventoUpdate,
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Evento>>

    @GET("convidados_eventos")
    suspend fun buscarConvidadosPorEvento(
        @Query("evento_id") idEventoFiltro: String,
        @Query("select") select: String = "*",
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Response<List<Convidado>>

    @GET("rest/v1/emprestimos?status=eq.Pendente&select=*,usuarios(*),livros(*)")
    fun listarAprovacoes(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearer: String
    ): Call<List<AprovacaoEmprestimo>>
}
