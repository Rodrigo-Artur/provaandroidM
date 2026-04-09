package com.example.prova.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prova.data.entity.Categoria
import com.example.prova.data.entity.Tarefa
import com.example.prova.databinding.FragmentDashboardBinding
import com.example.prova.ui.adapter.TarefaAdapter
import com.example.prova.ui.adapter.TarefaListItem
import com.example.prova.ui.viewmodel.TarefaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TarefaViewModel by activityViewModels()
    private lateinit var adapter: TarefaAdapter
    private val categoriasOcultas = mutableSetOf<String>()

    private val ordensCategoria = listOf(
        "⚠️ Atrasadas",
        "🔄 Tarefas Diárias",
        "🔥 Prioridade Alta",
        "📅 Hoje",
        "🌅 Para o Próximo Dia",
        "📆 Para os Próximos 7 Dias"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.allTarefas.observe(viewLifecycleOwner) { atualizarEcra() }
        viewModel.allCategorias.observe(viewLifecycleOwner) { atualizarEcra() }
    }

    private fun atualizarEcra() {
        val tarefas = viewModel.allTarefas.value ?: return
        val categorias = viewModel.allCategorias.value ?: emptyList()
        
        val totalDeTarefas = tarefas.size
        val concluidas = tarefas.count { it.status == "Concluída" }
        
        val produtividade = if (totalDeTarefas > 0) (concluidas.toFloat() / totalDeTarefas.toFloat()) * 100 else 0f
        binding.progressBar.progress = produtividade.toInt()
        binding.tvProdutividade.text = "${produtividade.toInt()}%"

        atualizarSubgraficos(tarefas)

        val pendentes = tarefas.filter { it.status != "Concluída" }
        val listaAgrupada = agruparPendentesParaDashboard(pendentes, categorias)
        adapter.setItems(listaAgrupada)
    }

    private fun classificarTarefa(tarefa: Tarefa, hoje: Date, sdf: SimpleDateFormat): String? {
        if (tarefa.limitDate != "Sem prazo") {
            try {
                val dataTarefa = sdf.parse(tarefa.limitDate)
                if (dataTarefa != null && (dataTarefa.time - hoje.time) / (1000 * 60 * 60 * 24) < 0L) {
                    return "⚠️ Atrasadas"
                }
            } catch (e: Exception) {}
        }
        if (tarefa.isDaily) return "🔄 Tarefas Diárias"
        if (tarefa.prioridade == "Alta") return "🔥 Prioridade Alta"

        if (tarefa.limitDate != "Sem prazo") {
            try {
                val dataTarefa = sdf.parse(tarefa.limitDate)
                if (dataTarefa != null) {
                    val diffDias = (dataTarefa.time - hoje.time) / (1000 * 60 * 60 * 24)
                    return when (diffDias) {
                        0L -> "📅 Hoje"
                        1L -> "🌅 Para o Próximo Dia"
                        in 2L..7L -> "📆 Para os Próximos 7 Dias"
                        else -> null
                    }
                }
            } catch (e: Exception) {}
        }
        return null
    }

    private fun atualizarSubgraficos(tarefas: List<Tarefa>) {
        binding.llSubgraficos.removeAllViews() 

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoje = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time

        val contagem = mutableMapOf<String, Pair<Int, Int>>() 
        ordensCategoria.forEach { contagem[it] = Pair(0, 0) }

        for (tarefa in tarefas) {
            val categoria = classificarTarefa(tarefa, hoje, sdf)
            if (categoria != null) {
                val isConcluida = tarefa.status == "Concluída"
                val atual = contagem[categoria]!!
                contagem[categoria] = Pair(atual.first + 1, atual.second + if (isConcluida) 1 else 0)
            }
        }

        for (nomeCat in ordensCategoria) {
            val stats = contagem[nomeCat]!!
            if (stats.first > 0) {
                val percentagem = (stats.second.toFloat() / stats.first.toFloat() * 100).toInt()

                val tv = TextView(requireContext()).apply {
                    text = "$nomeCat: $percentagem% concluído (${stats.second}/${stats.first})"
                    textSize = 14f
                    setPadding(0, 16, 0, 4)
                }

                val pb = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
                    max = 100
                    progress = percentagem
                    progressTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2196F3")) 
                }

                binding.llSubgraficos.addView(tv)
                binding.llSubgraficos.addView(pb)
            }
        }
    }

    private fun agruparPendentesParaDashboard(pendentes: List<Tarefa>, categorias: List<Categoria>): List<TarefaListItem> {
        val mapCores = categorias.associate { it.id to it.colorHex }
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoje = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.time

        val grupos = mutableMapOf<String, MutableList<Tarefa>>()
        ordensCategoria.forEach { grupos[it] = mutableListOf() }

        for (tarefa in pendentes) {
            val categoria = classificarTarefa(tarefa, hoje, sdf)
            if (categoria != null) {
                grupos[categoria]?.add(tarefa)
            }
        }

        val listaFinal = mutableListOf<TarefaListItem>()
        for (nomeCat in ordensCategoria) {
            val tarefasDoGrupo = grupos[nomeCat]!!
            if (tarefasDoGrupo.isNotEmpty()) {
                val isExpanded = !categoriasOcultas.contains(nomeCat)
                listaFinal.add(TarefaListItem.Header(nomeCat, isExpanded))
                if (isExpanded) {
                    // Mapeia passando a cor da Categoria Original da Tarefa
                    listaFinal.addAll(tarefasDoGrupo.map { TarefaListItem.Item(it, mapCores[it.categoriaID] ?: "#E0E0E0") })
                }
            }
        }
        return listaFinal
    }

    private fun setupRecyclerView() {
        adapter = TarefaAdapter(
            onClick = { },
            onLongClick = { tarefaClicada ->
                viewModel.updateTarefa(tarefaClicada.copy(status = "Concluída"))
                Toast.makeText(requireContext(), "Boa! Tarefa Concluída!", Toast.LENGTH_SHORT).show()
            },
            onHeaderClick = { tituloClicado ->
                if (categoriasOcultas.contains(tituloClicado)) categoriasOcultas.remove(tituloClicado)
                else categoriasOcultas.add(tituloClicado)
                atualizarEcra()
            }
        )
        binding.rvTarefasPendentes.adapter = adapter
        binding.rvTarefasPendentes.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}