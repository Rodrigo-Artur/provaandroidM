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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaTarefasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Observa a lista de tarefas no Banco de Dados
        viewModel.allTarefas.observe(viewLifecycleOwner) { tarefas ->
            adapter.setTarefas(tarefas)
        }

        binding.fabAddTarefa.setOnClickListener {
            showAddTarefaDialog()
        }
    }

    private fun setupRecyclerView() {
        // Ao segurar uma tarefa, vamos marcá-la como concluída!
        adapter = TarefaAdapter { tarefaClicada ->
            val tarefaAtualizada = tarefaClicada.copy(status = "Concluída")
            viewModel.updateTarefa(tarefaAtualizada)
            Toast.makeText(requireContext(), "Tarefa Concluída!", Toast.LENGTH_SHORT).show()
        }
        
        binding.rvTarefas.adapter = adapter
        binding.rvTarefas.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showAddTarefaDialog() {
        val categorias = viewModel.allCategorias.value
        
        // Verifica se existe alguma categoria criada antes de deixar criar a tarefa
        if (categorias.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Crie uma Categoria primeiro no menu 'Categorias'!", Toast.LENGTH_LONG).show()
            return
        }

        val context = requireContext()
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val inputTitulo = EditText(context)
        inputTitulo.hint = "Título da Tarefa"
        layout.addView(inputTitulo)

        val inputDesc = EditText(context)
        inputDesc.hint = "Descrição"
        layout.addView(inputDesc)

        // Usa a primeira categoria como padrão para simplificar
        val categoriaPadrao = categorias[0]

        AlertDialog.Builder(context)
            .setTitle("Nova Tarefa")
            .setMessage("Categoria vinculada: ${categoriaPadrao.nome}")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val titulo = inputTitulo.text.toString()
                val desc = inputDesc.text.toString()

                if (titulo.isNotEmpty()) {
                    val novaTarefa = Tarefa(
                        titulo = titulo,
                        descricao = desc,
                        categoriaID = categoriaPadrao.id,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}