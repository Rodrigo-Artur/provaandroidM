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

    /**
     * Atualiza a lista de categorias do adapter e notifica a mudança para atualizar a tela.
     */
    fun setCategorias(newCategorias: List<Categoria>) {
        this.categorias = newCategorias
        notifyDataSetChanged()
    }

    /**
     * Cria uma nova view inflando o layout do item da categoria para a lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriaViewHolder(binding)
    }

    /**
     * Preenche os dados da categoria no ViewHolder de acordo com a sua posição na lista.
     */
    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    /**
     * Retorna o número total de itens na lista de categorias.
     */
    override fun getItemCount(): Int = categorias.size

    inner class CategoriaViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Adiciona os dados de nome e cor à view da categoria, além dos eventos de click.
         */
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