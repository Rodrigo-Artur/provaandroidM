package com.example.prova.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prova.data.entity.Categoria
import com.example.prova.databinding.ItemCategoriaBinding

class CategoriaAdapter(
    private val onClick: (Categoria) -> Unit,       // NOVO: Toque simples para editar
    private val onLongClick: (Categoria) -> Unit    // Toque longo para deletar
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var categorias = emptyList<Categoria>()

    fun setCategorias(categorias: List<Categoria>) {
        this.categorias = categorias
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    override fun getItemCount(): Int = categorias.size

    inner class CategoriaViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria) {
            binding.tvNomeCategoria.text = categoria.nome
            try {
                binding.viewColor.setBackgroundColor(Color.parseColor(categoria.colorHex))
            } catch (e: Exception) {
                binding.viewColor.setBackgroundColor(Color.GRAY)
            }

            // Configuração dos Toques
            binding.root.setOnClickListener { onClick(categoria) }
            binding.root.setOnLongClickListener {
                onLongClick(categoria)
                true
            }
        }
    }
}