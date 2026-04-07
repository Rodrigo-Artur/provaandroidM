package com.example.prova.ui.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prova.data.entity.Categoria
import com.example.prova.databinding.FragmentCategoriasBinding
import com.example.prova.ui.adapter.CategoriaAdapter
import com.example.prova.ui.viewmodel.TarefaViewModel

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TarefaViewModel by activityViewModels()
    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Observa o banco de dados. Sempre que adicionar/remover algo, a tela atualiza na mesma hora!
        viewModel.allCategorias.observe(viewLifecycleOwner) { categorias ->
            adapter.setCategorias(categorias)
        }

        // Clique no botão de Adicionar
        binding.fabAddCategoria.setOnClickListener {
            showAddCategoriaDialog()
        }
    }

    private fun setupRecyclerView() {
        // Inicializa o Adapter. O bloco dentro de {} diz o que acontece ao segurar o dedo no item.
        adapter = CategoriaAdapter { categoriaClicada ->
            viewModel.deleteCategoria(categoriaClicada)
            Toast.makeText(requireContext(), "Categoria deletada!", Toast.LENGTH_SHORT).show()
        }
        
        binding.rvCategorias.adapter = adapter
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showAddCategoriaDialog() {
        val context = requireContext()
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val inputNome = EditText(context)
        inputNome.hint = "Nome da Categoria (Ex: Trabalho)"
        layout.addView(inputNome)

        val inputCor = EditText(context)
        inputCor.hint = "Cor Hexadecimal (Ex: #FF0000)"
        inputCor.setText("#2196F3") // Sugestão azul por padrão
        layout.addView(inputCor)

        AlertDialog.Builder(context)
            .setTitle("Nova Categoria")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val nome = inputNome.text.toString()
                val cor = inputCor.text.toString()
                
                if (nome.isNotEmpty() && cor.isNotEmpty()) {
                    val novaCategoria = Categoria(nome = nome, colorHex = cor)
                    viewModel.insertCategoria(novaCategoria)
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}