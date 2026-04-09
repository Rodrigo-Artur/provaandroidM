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
import com.example.prova.data.entity.Tarefa
import com.example.prova.databinding.FragmentListaTarefasBinding
import com.example.prova.ui.adapter.TarefaAdapter
import com.example.prova.ui.adapter.TarefaListItem
import com.example.prova.ui.viewmodel.TarefaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        // Observamos as duas tabelas para termos as tarefas E as cores
        viewModel.allTarefas.observe(viewLifecycleOwner) { atualizarLista() }
        viewModel.allCategorias.observe(viewLifecycleOwner) { atualizarLista() }

        binding.fabAddTarefa.setOnClickListener { showAddTarefaDialog() }
    }

    private fun atualizarLista() {
        val tarefas = viewModel.allTarefas.value ?: return
        val categorias = viewModel.allCategorias.value ?: emptyList()
        val listaAgrupada = agruparTarefasPorData(tarefas, categorias)
        adapter.setItems(listaAgrupada)
    }

    private fun agruparTarefasPorData(tarefas: List<Tarefa>, categorias: List<Categoria>): List<TarefaListItem> {
        val mapCores = categorias.associate { it.id to it.colorHex } // Cria um dicionário Rápido ID -> Cor
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoje = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time

        val atrasadas = mutableListOf<Tarefa>()
        val paraHoje = mutableListOf<Tarefa>()
        val paraSemana = mutableListOf<Tarefa>()
        val paraMes = mutableListOf<Tarefa>()
        val semPrazo = mutableListOf<Tarefa>()
        val concluidas = mutableListOf<Tarefa>()

        for (tarefa in tarefas) {
            if (tarefa.status == "Concluída") { concluidas.add(tarefa); continue }
            if (tarefa.limitDate == "Sem prazo") { semPrazo.add(tarefa); continue }
            try {
                val dataTarefa = sdf.parse(tarefa.limitDate)
                if (dataTarefa != null) {
                    val diffDias = (dataTarefa.time - hoje.time) / (1000 * 60 * 60 * 24)
                    when {
                        diffDias < 0 -> atrasadas.add(tarefa)
                        diffDias == 0L -> paraHoje.add(tarefa)
                        diffDias in 1..7 -> paraSemana.add(tarefa)
                        diffDias in 8..30 -> paraMes.add(tarefa)
                        else -> semPrazo.add(tarefa)
                    }
                }
            } catch (e: Exception) { semPrazo.add(tarefa) }
        }

        val listaFinal = mutableListOf<TarefaListItem>()
        
        // Mapeia e injeta a cor correspondente
        if (atrasadas.isNotEmpty()) {
            listaFinal.add(TarefaListItem.Header("⚠️ Atrasadas"))
            listaFinal.addAll(atrasadas.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
        }
        if (paraHoje.isNotEmpty()) {
            listaFinal.add(TarefaListItem.Header("📅 Hoje"))
            listaFinal.addAll(paraHoje.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
        }
        if (paraSemana.isNotEmpty()) {
            listaFinal.add(TarefaListItem.Header("📆 Esta Semana"))
            listaFinal.addAll(paraSemana.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
        }
        if (paraMes.isNotEmpty()) {
            listaFinal.add(TarefaListItem.Header("🗓️ Este Mês"))
            listaFinal.addAll(paraMes.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
        }
        if (semPrazo.isNotEmpty()) {
            listaFinal.add(TarefaListItem.Header("📌 Sem Prazo / Futuro"))
            listaFinal.addAll(semPrazo.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
        }
        if (concluidas.isNotEmpty()) {
            listaFinal.add(TarefaListItem.Header("✅ Concluídas"))
            listaFinal.addAll(concluidas.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
        }

        return listaFinal
    }

    private fun setupRecyclerView() {
        adapter = TarefaAdapter(
            onClick = { showEditTarefaDialog(it) },
            onLongClick = { tarefa ->
                viewModel.updateTarefa(tarefa.copy(status = "Concluída"))
                Toast.makeText(requireContext(), "Tarefa Concluída!", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvTarefas.adapter = adapter
        binding.rvTarefas.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showAddTarefaDialog() {
        val categorias = viewModel.allCategorias.value
        if (categorias.isNullOrEmpty()) { Toast.makeText(requireContext(), "Crie uma Categoria primeiro!", Toast.LENGTH_LONG).show(); return }
        val context = requireContext()
        val layout = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL; setPadding(50, 40, 50, 10) }
        val inputTitulo = EditText(context).apply { hint = "Título da Tarefa" }
        val inputDesc = EditText(context).apply { hint = "Descrição" }
        val labelCategoria = android.widget.TextView(context).apply { text = "Categoria:"; setPadding(0, 20, 0, 0) }
        val spinnerCategoria = android.widget.Spinner(context)
        spinnerCategoria.adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categorias.map { it.nome })
        val labelPrioridade = android.widget.TextView(context).apply { text = "Prioridade:"; setPadding(0, 20, 0, 0) }
        val spinnerPrioridade = android.widget.Spinner(context)
        val prioridades = arrayOf("Baixa", "Média", "Alta")
        spinnerPrioridade.adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, prioridades)
        spinnerPrioridade.setSelection(1) 
        var dataSelecionada = "Sem prazo"
        val tvPrazo = android.widget.TextView(context).apply {
            text = "📅 Definir Prazo (Opcional)"
            setPadding(0, 40, 0, 20)
            setTextColor(android.graphics.Color.parseColor("#2196F3"))
            textSize = 16f
        }
        tvPrazo.setOnClickListener {
            val calendar = Calendar.getInstance()
            android.app.DatePickerDialog(context, { _, ano, mes, dia ->
                dataSelecionada = "${dia.toString().padStart(2, '0')}/${(mes + 1).toString().padStart(2, '0')}/$ano"
                tvPrazo.text = "📅 Prazo: $dataSelecionada"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        val cbDiaria = android.widget.CheckBox(context).apply { text = "Repete diariamente" }

        layout.apply {
            addView(inputTitulo); addView(inputDesc)
            addView(labelCategoria); addView(spinnerCategoria)
            addView(labelPrioridade); addView(spinnerPrioridade)
            addView(tvPrazo); addView(cbDiaria)
        }

        AlertDialog.Builder(context).setTitle("Nova Tarefa").setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                if (inputTitulo.text.isNotEmpty()) {
                    viewModel.insertTarefa(Tarefa(
                        titulo = inputTitulo.text.toString(), descricao = inputDesc.text.toString(),
                        categoriaID = categorias[spinnerCategoria.selectedItemPosition].id,
                        prioridade = prioridades[spinnerPrioridade.selectedItemPosition],
                        status = "Pendente", limitDate = dataSelecionada, isDaily = cbDiaria.isChecked,
                        createdAt = System.currentTimeMillis()
                    ))
                } else Toast.makeText(context, "O título é obrigatório", Toast.LENGTH_SHORT).show()
            }.setNegativeButton("Cancelar", null).show()
    }

    private fun showEditTarefaDialog(tarefa: Tarefa) {
        val categorias = viewModel.allCategorias.value
        if (categorias.isNullOrEmpty()) return
        val context = requireContext()
        val layout = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL; setPadding(50, 40, 50, 10) }
        val inputTitulo = EditText(context).apply { setText(tarefa.titulo) }
        val inputDesc = EditText(context).apply { setText(tarefa.descricao) }
        val labelCategoria = android.widget.TextView(context).apply { text = "Categoria:"; setPadding(0, 20, 0, 0) }
        val spinnerCategoria = android.widget.Spinner(context)
        spinnerCategoria.adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categorias.map { it.nome })
        val posCat = categorias.indexOfFirst { it.id == tarefa.categoriaID }
        if (posCat >= 0) spinnerCategoria.setSelection(posCat)
        val labelPrioridade = android.widget.TextView(context).apply { text = "Prioridade:"; setPadding(0, 20, 0, 0) }
        val spinnerPrioridade = android.widget.Spinner(context)
        val prioridades = arrayOf("Baixa", "Média", "Alta")
        spinnerPrioridade.adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, prioridades)
        spinnerPrioridade.setSelection(prioridades.indexOf(tarefa.prioridade).takeIf { it >= 0 } ?: 1)
        var dataSelecionada = tarefa.limitDate
        val tvPrazo = android.widget.TextView(context).apply {
            text = if (tarefa.limitDate == "Sem prazo") "📅 Definir Prazo (Opcional)" else "📅 Prazo: ${tarefa.limitDate}"
            setPadding(0, 40, 0, 20); setTextColor(android.graphics.Color.parseColor("#2196F3")); textSize = 16f
        }
        tvPrazo.setOnClickListener {
            val calendar = Calendar.getInstance()
            android.app.DatePickerDialog(context, { _, ano, mes, dia ->
                dataSelecionada = "${dia.toString().padStart(2, '0')}/${(mes + 1).toString().padStart(2, '0')}/$ano"
                tvPrazo.text = "📅 Prazo: $dataSelecionada"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        val cbDiaria = android.widget.CheckBox(context).apply { text = "Repete diariamente"; isChecked = tarefa.isDaily }

        layout.apply {
            addView(inputTitulo); addView(inputDesc)
            addView(labelCategoria); addView(spinnerCategoria)
            addView(labelPrioridade); addView(spinnerPrioridade)
            addView(tvPrazo); addView(cbDiaria)
        }

        AlertDialog.Builder(context).setTitle("Editar Tarefa").setView(layout)
            .setPositiveButton("Atualizar") { _, _ ->
                if (inputTitulo.text.isNotEmpty()) {
                    viewModel.updateTarefa(tarefa.copy(
                        titulo = inputTitulo.text.toString(), descricao = inputDesc.text.toString(),
                        categoriaID = categorias[spinnerCategoria.selectedItemPosition].id,
                        prioridade = prioridades[spinnerPrioridade.selectedItemPosition],
                        limitDate = dataSelecionada, isDaily = cbDiaria.isChecked
                    ))
                }
            }.setNeutralButton("Excluir") { _, _ -> viewModel.deleteTarefa(tarefa) }.setNegativeButton("Cancelar", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}