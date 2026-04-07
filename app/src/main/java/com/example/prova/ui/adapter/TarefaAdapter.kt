package com.example.prova.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prova.data.entity.Tarefa
import com.example.prova.databinding.ItemTarefaBinding

class TarefaAdapter(
    private val onLongClick: (Tarefa) -> Unit // Ação para deletar ou concluir
) : RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    private var tarefas = emptyList<Tarefa>()

    fun setTarefas(tarefas: List<Tarefa>) {
        this.tarefas = tarefas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
        val binding = ItemTarefaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TarefaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        holder.bind(tarefas[position])
    }

    override fun getItemCount(): Int = tarefas.size

    inner class TarefaViewHolder(private val binding: ItemTarefaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tarefa: Tarefa) {
            binding.tvTituloTarefa.text = tarefa.titulo
            binding.tvDescricaoTarefa.text = tarefa.descricao
            binding.tvStatusTarefa.text = tarefa.status

            // Muda a cor do texto dependendo se está concluída ou pendente
            if (tarefa.status == "Concluída") {
                binding.tvStatusTarefa.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Verde
            } else {
                binding.tvStatusTarefa.setTextColor(android.graphics.Color.parseColor("#FF9800")) // Laranja
            }

            binding.root.setOnLongClickListener {
                onLongClick(tarefa)
                true
            }
        }
    }
}