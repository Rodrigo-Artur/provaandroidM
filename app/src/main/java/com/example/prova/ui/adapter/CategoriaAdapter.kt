package com.example.prova.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prova.data.entity.Categoria
import com.example.prova.databinding.ItemCategoriaBinding

class CategoriaAdapter(
    private val onClick: (Categoria) -> Unit,
    private val onLongClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    private var categorias = emptyList<Categoria>()

    fun setCategorias(newCategorias: List<Categoria>) {
        this.categorias = newCategorias
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
                val circle = GradientDrawable()
                circle.shape = GradientDrawable.OVAL
                circle.setColor(Color.parseColor(categoria.colorHex))
                binding.viewColor.background = circle
            } catch (e: Exception) {
                val circle = GradientDrawable()
                circle.shape = GradientDrawable.OVAL
                circle.setColor(Color.LTGRAY)
                binding.viewColor.background = circle
            }

            binding.root.setOnClickListener { onClick(categoria) }
            binding.root.setOnLongClickListener {
                onLongClick(categoria)
                true
            }
        }
    }
}