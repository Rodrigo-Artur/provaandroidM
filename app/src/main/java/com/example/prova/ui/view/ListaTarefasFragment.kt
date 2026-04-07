package com.example.prova.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.prova.databinding.FragmentListaTarefasBinding
import com.example.prova.ui.viewmodel.TarefaViewModel

class ListaTarefasFragment : Fragment() {

    private var _binding: FragmentListaTarefasBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TarefaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaTarefasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Exemplo: Observando as tarefas (implementaremos a lista visual no próximo passo)
        viewModel.allTarefas.observe(viewLifecycleOwner) { tarefas ->
            // Atualizar o RecyclerView aqui
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}