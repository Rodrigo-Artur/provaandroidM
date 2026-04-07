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
    
    // Compartilha o mesmo ViewModel com a Activity e outros Fragments
    private val viewModel: TarefaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aqui colocaremos a lógica dos gráficos e estatísticas depois
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}