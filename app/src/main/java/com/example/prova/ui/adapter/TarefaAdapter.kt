package com.example.prova.ui.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prova.R
import com.example.prova.data.entity.Tarefa
import com.example.prova.databinding.ItemTarefaBinding

sealed class TarefaListItem {
    data class Header(val title: String, val isExpanded: Boolean = true) : TarefaListItem()
    // NOVO: Adicionada a corCategoria para podermos pintar a sombra
    data class Item(val tarefa: Tarefa, val corCategoria: String) : TarefaListItem()
}

class TarefaAdapter(
    private val onClick: (Tarefa) -> Unit,
    private val onLongClick: (Tarefa) -> Unit,
    private val onHeaderClick: (String) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<TarefaListItem>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun setItems(newItems: List<TarefaListItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TarefaListItem.Header -> TYPE_HEADER
            is TarefaListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val binding = ItemTarefaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TarefaViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is HeaderViewHolder && item is TarefaListItem.Header) {
            holder.bind(item.title, item.isExpanded)
        } else if (holder is TarefaViewHolder && item is TarefaListItem.Item) {
            holder.bind(item.tarefa, item.corCategoria) // Passamos a cor aqui!
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvHeader: TextView = view.findViewById(R.id.tvHeaderTitle)
        fun bind(title: String, isExpanded: Boolean) {
            val seta = if (isExpanded) "▼" else "▶"
            tvHeader.text = "$seta $title"
            itemView.setOnClickListener { onHeaderClick(title) }
        }
    }

    inner class TarefaViewHolder(private val binding: ItemTarefaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tarefa: Tarefa, corCategoria: String) {
            binding.tvTituloTarefa.text = tarefa.titulo
            val textoDiaria = if (tarefa.isDaily) " | 🔄 Diária" else ""
            binding.tvDescricaoTarefa.text = "${tarefa.descricao}\nPrazo: ${tarefa.limitDate} | Prioridade: ${tarefa.prioridade}$textoDiaria"
            binding.tvStatusTarefa.text = tarefa.status

            if (tarefa.status == "Concluída") {
                binding.tvStatusTarefa.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                binding.tvStatusTarefa.setTextColor(Color.parseColor("#FF9800"))
            }

            // --- EFEITO DE SOMBRA COLORIDA NEON ---
            try {
                val color = Color.parseColor(corCategoria)
                // Apenas em Android 9 (Pie) ou superior é que a sombra colorida é suportada
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    binding.root.outlineSpotShadowColor = color
                    binding.root.outlineAmbientShadowColor = color
                    // Aumentamos a elevação programaticamente para a sombra "saltar" mais
                    binding.root.cardElevation = 10f * binding.root.context.resources.displayMetrics.density
                }
            } catch (e: Exception) { }

            binding.root.setOnClickListener { onClick(tarefa) }
            binding.root.setOnLongClickListener { onLongClick(tarefa); true }
        }
    }
}