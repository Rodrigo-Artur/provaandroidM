package com.example.prova.ui.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.allCategorias.observe(viewLifecycleOwner) { categorias ->
            adapter.setCategorias(categorias)
        }

        binding.fabAddCategoria.setOnClickListener { showAddCategoriaDialog() }
    }

    private fun setupRecyclerView() {
        adapter = CategoriaAdapter(
            onClick = { categoriaClicada ->
                showEditCategoriaDialog(categoriaClicada)
            },
            onLongClick = { categoriaClicada ->
                viewModel.deleteCategoria(categoriaClicada)
                Toast.makeText(requireContext(), "Categoria deletada!", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvCategorias.adapter = adapter
        binding.rvCategorias.layoutManager = LinearLayoutManager(requireContext())
    }

    // --- MOTOR DA PALETA DE CORES ---
    private fun criarSeletorDeCor(context: Context, corInicial: String, onColorSelected: (String) -> Unit): View {
        val cores = arrayOf(
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
            "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000"
        )

        val gridLayout = GridLayout(context).apply {
            columnCount = 5
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        var corAtual = corInicial
        val viewsDeCor = mutableListOf<View>()
        val density = resources.displayMetrics.density

        for (cor in cores) {
            val colorView = View(context).apply {
                val tamanho = (44 * density).toInt()
                val margem = (8 * density).toInt()
                
                layoutParams = GridLayout.LayoutParams().apply {
                    width = tamanho
                    height = tamanho
                    setMargins(margem, margem, margem, margem)
                }

                val shape = GradientDrawable()
                shape.shape = GradientDrawable.OVAL
                shape.setColor(Color.parseColor(cor))
                
                if (cor == corAtual || (corAtual.isEmpty() && cor == "#2196F3")) {
                    shape.setStroke((4 * density).toInt(), Color.BLACK)
                } else {
                    shape.setStroke((1 * density).toInt(), Color.LTGRAY)
                }
                background = shape

                setOnClickListener {
                    corAtual = cor
                    onColorSelected(cor)
                    
                    viewsDeCor.forEach { v ->
                        val bg = v.background as GradientDrawable
                        bg.setStroke((1 * density).toInt(), Color.LTGRAY)
                    }
                    shape.setStroke((4 * density).toInt(), Color.BLACK)
                }
            }
            viewsDeCor.add(colorView)
            gridLayout.addView(colorView)
        }

        return ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (200 * density).toInt()
            ).apply { setMargins(0, 20, 0, 0) }
            addView(gridLayout)
        }
    }

    private fun showAddCategoriaDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputNome = EditText(context).apply { hint = "Nome da Categoria (Ex: Trabalho)" }
        layout.addView(inputNome)

        val labelCor = TextView(context).apply { 
            text = "Selecione a Cor:"
            setPadding(0, 30, 0, 10)
        }
        layout.addView(labelCor)

        var corSelecionada = "#2196F3"
        val seletorCores = criarSeletorDeCor(context, corSelecionada) { novaCor ->
            corSelecionada = novaCor
        }
        layout.addView(seletorCores)

        AlertDialog.Builder(context)
            .setTitle("Nova Categoria")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val nome = inputNome.text.toString()
                if (nome.isNotEmpty()) {
                    viewModel.insertCategoria(Categoria(nome = nome, colorHex = corSelecionada))
                } else {
                    Toast.makeText(context, "O nome é obrigatório!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditCategoriaDialog(categoria: Categoria) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputNome = EditText(context).apply { setText(categoria.nome) }
        layout.addView(inputNome)

        val labelCor = TextView(context).apply { 
            text = "Selecione a Cor:"
            setPadding(0, 30, 0, 10)
        }
        layout.addView(labelCor)

        var corSelecionada = categoria.colorHex
        val seletorCores = criarSeletorDeCor(context, corSelecionada) { novaCor ->
            corSelecionada = novaCor
        }
        layout.addView(seletorCores)

        AlertDialog.Builder(context)
            .setTitle("Editar Categoria")
            .setView(layout)
            .setPositiveButton("Atualizar") { _, _ ->
                val nome = inputNome.text.toString()
                if (nome.isNotEmpty()) {
                    // ---> MUDOU AQUI: Usar updateCategoria em vez de insertCategoria <---
                    viewModel.updateCategoria(categoria.copy(nome = nome, colorHex = corSelecionada))
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