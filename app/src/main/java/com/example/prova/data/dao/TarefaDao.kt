package com.example.prova.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import com.example.prova.data.entity.Tarefa

@Dao
interface TarefaDao {
    @Query("SELECT * FROM tarefa")
    fun getAllTarefas(): Flow<List<Tarefa>>

    @Insert
    suspend fun insert(tarefa: Tarefa)

    @Update
    suspend fun update(tarefa: Tarefa)

    @Delete
    suspend fun delete(tarefa: Tarefa)
}