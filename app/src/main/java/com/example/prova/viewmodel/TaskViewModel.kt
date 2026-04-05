package com.example.prova.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prova.data.entity.Tarefa
import com.example.prova.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Mantém o estado da lista de tarefas reativo
    val allTarefas: StateFlow<List<Tarefa>> = repository.allTarefas
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(tarefa: Tarefa) = viewModelScope.launch {
        repository.insert(tarefa)
    }

    fun update(tarefa: Tarefa) = viewModelScope.launch {
        repository.update(tarefa)
    }

    fun delete(tarefa: Tarefa) = viewModelScope.launch {
        repository.delete(tarefa)
    }
}