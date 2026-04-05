package com.example.prova.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Prioridade { BAIXA, MEDIA, ALTA }
enum class Status { PENDENTE, CONCLUIDA }

@Entity(tableName = "tarefa")
data class Tarefa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descricao: String,
    val categoriaID: Long,
    val prioridade: Prioridade,
    val status: Status,
    val limitDate: String,
    val createdAt: Long
)