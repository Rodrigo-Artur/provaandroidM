package com.example.prova.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.prova.data.db.AppDatabase
import com.example.prova.data.entity.Categoria
import com.example.prova.data.entity.Tarefa
import com.example.prova.data.repository.CategoriaRepository
import com.example.prova.data.repository.TarefaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TarefaViewModel(application: Application) : AndroidViewModel(application) {

    private val tarefaRepository: TarefaRepository
    private val categoriaRepository: CategoriaRepository

    val allTarefas: LiveData<List<Tarefa>>
    val tarefasPendentes: LiveData<List<Tarefa>>
    val tarefasConcluidas: LiveData<List<Tarefa>>
    val allCategorias: LiveData<List<Categoria>>

    init {
        // Inicializa o banco de dados e os DAOs
        val database = AppDatabase.getDatabase(application)
        
        // Inicializa os repositórios
        tarefaRepository = TarefaRepository(database.tarefaDao())
        categoriaRepository = CategoriaRepository(database.categoriaDao())

        // Carrega as listas de dados
        allTarefas = tarefaRepository.allTarefas
        tarefasPendentes = tarefaRepository.tarefasPendentes
        tarefasConcluidas = tarefaRepository.tarefasConcluidas
        allCategorias = categoriaRepository.allCategorias
    }

    // ======================== TAREFAS ========================
    // O viewModelScope.launch garante que a operação no banco não trave a tela (Main Thread)
    fun insertTarefa(tarefa: Tarefa) = viewModelScope.launch(Dispatchers.IO) {
        tarefaRepository.insert(tarefa)
    }

    fun updateTarefa(tarefa: Tarefa) = viewModelScope.launch(Dispatchers.IO) {
        tarefaRepository.update(tarefa)
    }

    fun deleteTarefa(tarefa: Tarefa) = viewModelScope.launch(Dispatchers.IO) {
        tarefaRepository.delete(tarefa)
    }

    // ======================== CATEGORIAS ========================
    fun insertCategoria(categoria: Categoria) = viewModelScope.launch(Dispatchers.IO) {
        categoriaRepository.insert(categoria)
    }

    fun deleteCategoria(categoria: Categoria) = viewModelScope.launch(Dispatchers.IO) {
        categoriaRepository.delete(categoria)
    }
}