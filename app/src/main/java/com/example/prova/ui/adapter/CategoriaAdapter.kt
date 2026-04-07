package com.example.prova.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prova.data.entity.Categoria
import com.example.prova.databinding.ItemCategoriaBinding

class CategoriaAdapter(
    private val onLongClick: (Categoria) -> Unit // Ação para deletar ao segurar
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var categorias = emptyList<Categoria>()

    fun setCategorias(categorias: List<Categoria>) {
        this.categorias = categorias
        notifyDataSetChanged() // Avisa a tela que os dados mudaram
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val current = categorias[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int = categorias.size

    inner class CategoriaViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria) {
            binding.tvNomeCategoria.text = categoria.nome
            try {
                // Tenta aplicar a cor Hexadecimal digitada
                binding.viewColor.setBackgroundColor(Color.parseColor(categoria.colorHex))
            } catch (e: Exception) {
                binding.viewColor.setBackgroundColor(Color.GRAY) // Cor padrão caso digitem errado
            }

            // Ao segurar o dedo no item, aciona a função de deletar
            binding.root.setOnLongClickListener {
                onLongClick(categoria)
                true
            }
        }
    }
}