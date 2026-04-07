package com.example.prova.data.repository

import androidx.lifecycle.LiveData
import com.example.prova.data.dao.TarefaDao
import com.example.prova.data.entity.Tarefa

class TarefaRepository(private val tarefaDao: TarefaDao) {

    val allTarefas: LiveData<List<Tarefa>> = tarefaDao.getAllTarefas()
    val tarefasPendentes: LiveData<List<Tarefa>> = tarefaDao.getTarefasPendentes()
    val tarefasConcluidas: LiveData<List<Tarefa>> = tarefaDao.getTarefasConcluidas()

    suspend fun insert(tarefa: Tarefa) {
        tarefaDao.insertTarefa(tarefa)
    }

    suspend fun update(tarefa: Tarefa) {
        tarefaDao.updateTarefa(tarefa)
    }

    suspend fun delete(tarefa: Tarefa) {
        tarefaDao.deleteTarefa(tarefa)
    }
}