package com.example.prova.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.prova.databinding.FragmentDashboardBinding
import com.example.prova.ui.viewmodel.TarefaViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Usamos o mesmo ViewModel para partilhar os dados com as outras janelas
    private val viewModel: TarefaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fica "à escuta" das mudanças na lista de tarefas para atualizar os cálculos
        viewModel.allTarefas.observe(viewLifecycleOwner) { tarefas ->
            val totalDeTarefas = tarefas.size
            
            // Conta quantas têm o status "Concluída" e "Pendente"
            val concluidas = tarefas.count { it.status == "Concluída" }
            val pendentes = tarefas.count { it.status == "Pendente" }

            // Calcula a percentagem de produtividade (Regra de três simples)
            val produtividade = if (totalDeTarefas > 0) {
                (concluidas.toFloat() / totalDeTarefas.toFloat()) * 100
            } else {
                0f // Evita erro de divisão por zero se não houver tarefas
            }

            // Escreve os resultados no ecrã
            binding.tvConcluidas.text = concluidas.toString()
            binding.tvPendentes.text = pendentes.toString()
            binding.tvProdutividade.text = String.format("%.1f%%", produtividade)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}