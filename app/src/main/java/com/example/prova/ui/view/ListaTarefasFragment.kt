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
import com.example.prova.data.entity.Tarefa
import com.example.prova.databinding.FragmentListaTarefasBinding
import com.example.prova.ui.adapter.TarefaAdapter
import com.example.prova.ui.viewmodel.TarefaViewModel

class ListaTarefasFragment : Fragment() {

    private var _binding: FragmentListaTarefasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TarefaViewModel by activityViewModels()
    private lateinit var adapter: TarefaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListaTarefasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.allTarefas.observe(viewLifecycleOwner) { tarefas ->
            adapter.setTarefas(tarefas)
        }

        binding.fabAddTarefa.setOnClickListener { showAddTarefaDialog() }
    }

    private fun setupRecyclerView() {
        adapter = TarefaAdapter(
            onClick = { tarefaClicada ->
                showEditTarefaDialog(tarefaClicada) // Chama a edição
            },
            onLongClick = { tarefaClicada ->
                val tarefaAtualizada = tarefaClicada.copy(status = "Concluída")
                viewModel.updateTarefa(tarefaAtualizada)
                Toast.makeText(requireContext(), "Tarefa Concluída!", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvTarefas.adapter = adapter
        binding.rvTarefas.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showAddTarefaDialog() {
        val categorias = viewModel.allCategorias.value
        if (categorias.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Crie uma Categoria primeiro!", Toast.LENGTH_LONG).show()
            return
        }

        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputTitulo = EditText(context).apply { hint = "Título da Tarefa" }
        val inputDesc = EditText(context).apply { hint = "Descrição" }
        val labelCategoria = android.widget.TextView(context).apply { 
            text = "Selecione a Categoria:"
            setPadding(0, 30, 0, 10)
        }

        val spinnerCategoria = android.widget.Spinner(context)
        spinnerCategoria.adapter = android.widget.ArrayAdapter(
            context, android.R.layout.simple_spinner_dropdown_item, categorias.map { it.nome }
        )

        layout.addView(inputTitulo)
        layout.addView(inputDesc)
        layout.addView(labelCategoria)
        layout.addView(spinnerCategoria)

        AlertDialog.Builder(context)
            .setTitle("Nova Tarefa")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val titulo = inputTitulo.text.toString()
                if (titulo.isNotEmpty()) {
                    val categoriaSelecionada = categorias[spinnerCategoria.selectedItemPosition]
                    val novaTarefa = Tarefa(
                        titulo = titulo,
                        descricao = inputDesc.text.toString(),
                        categoriaID = categoriaSelecionada.id,
                        prioridade = "Média",
                        status = "Pendente",
                        limitDate = "Sem prazo",
                        createdAt = System.currentTimeMillis()
                    )
                    viewModel.insertTarefa(novaTarefa)
                } else {
                    Toast.makeText(context, "O título é obrigatório", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditTarefaDialog(tarefa: Tarefa) {
        val categorias = viewModel.allCategorias.value
        if (categorias.isNullOrEmpty()) return

        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        // Pré-preenche com os dados antigos
        val inputTitulo = EditText(context).apply { setText(tarefa.titulo) }
        val inputDesc = EditText(context).apply { setText(tarefa.descricao) }
        val labelCategoria = android.widget.TextView(context).apply {
            text = "Selecione a Categoria:"
            setPadding(0, 30, 0, 10)
        }

        val spinnerCategoria = android.widget.Spinner(context)
        spinnerCategoria.adapter = android.widget.ArrayAdapter(
            context, android.R.layout.simple_spinner_dropdown_item, categorias.map { it.nome }
        )
        
        // Encontra a posição da categoria atual para pré-selecionar no Spinner
        val posicaoAtual = categorias.indexOfFirst { it.id == tarefa.categoriaID }
        if (posicaoAtual >= 0) {
            spinnerCategoria.setSelection(posicaoAtual)
        }

        layout.addView(inputTitulo)
        layout.addView(inputDesc)
        layout.addView(labelCategoria)
        layout.addView(spinnerCategoria)

        AlertDialog.Builder(context)
            .setTitle("Editar Tarefa")
            .setView(layout)
            .setPositiveButton("Atualizar") { _, _ ->
                val titulo = inputTitulo.text.toString()
                if (titulo.isNotEmpty()) {
                    val categoriaSelecionada = categorias[spinnerCategoria.selectedItemPosition]
                    // Mantém o ID e o Status originais, apenas atualiza os dados visíveis
                    val tarefaAtualizada = tarefa.copy(
                        titulo = titulo,
                        descricao = inputDesc.text.toString(),
                        categoriaID = categoriaSelecionada.id
                    )
                    viewModel.updateTarefa(tarefaAtualizada)
                }
            }
            .setNeutralButton("Excluir Tarefa") { _, _ ->
                viewModel.deleteTarefa(tarefa)
                Toast.makeText(context, "Tarefa excluída", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}