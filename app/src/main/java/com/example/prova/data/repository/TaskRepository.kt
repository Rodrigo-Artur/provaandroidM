package com.example.prova.data.repository

import com.example.prova.data.dao.TarefaDao
import com.example.prova.data.entity.Tarefa
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val tarefaDao: TarefaDao) {
    val allTarefas: Flow<List<Tarefa>> = tarefaDao.getAllTarefas()

    suspend fun insert(tarefa: Tarefa) {
        tarefaDao.insert(tarefa)
    }

    suspend fun update(tarefa: Tarefa) {
        tarefaDao.update(tarefa)
    }

    suspend fun delete(tarefa: Tarefa) {
        tarefaDao.delete(tarefa)
    }
}