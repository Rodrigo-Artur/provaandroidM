package com.example.prova.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prova.data.entity.Tarefa

@Dao
interface TarefaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarefa(tarefa: Tarefa)

    @Update
    suspend fun updateTarefa(tarefa: Tarefa)

    @Delete
    suspend fun deleteTarefa(tarefa: Tarefa)

    @Query("SELECT * FROM tabela_tarefa ORDER BY limitDate ASC")
    fun getAllTarefas(): LiveData<List<Tarefa>>

    @Query("SELECT * FROM tabela_tarefa WHERE status = 'Pendente'")
    fun getTarefasPendentes(): LiveData<List<Tarefa>>

    @Query("SELECT * FROM tabela_tarefa WHERE status = 'Concluida'")
    fun getTarefasConcluidas(): LiveData<List<Tarefa>>
}