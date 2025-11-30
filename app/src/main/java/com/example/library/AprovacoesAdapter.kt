class AprovacoesAdapter(
    private var listaAprovacoes: List<Aprovacao>,
    private val onItemClick: (Aprovacao) -> Unit
) : RecyclerView.Adapter<AprovacoesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescricao: TextView = view.findViewById(R.id.tvDescricaoReserva)
        val btnAprovar: AppCompatButton = view.findViewById(R.id.btnAprovarReserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aprovacao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val aprovacao = listaAprovacoes[position]
        holder.tvDescricao.text =
            "${aprovacao.usuario} está solicitando a reserva do livro “${aprovacao.livro}” para o período de ${aprovacao.periodo}."

        holder.btnAprovar.setOnClickListener {
            onItemClick(aprovacao)
        }
        holder.itemView.setOnClickListener {
            onItemClick(aprovacao)
        }
    }

    override fun getItemCount(): Int = listaAprovacoes.size

    fun atualizarLista(novaLista: List<Aprovacao>) {
        listaAprovacoes = novaLista
        notifyDataSetChanged()
    }
}
