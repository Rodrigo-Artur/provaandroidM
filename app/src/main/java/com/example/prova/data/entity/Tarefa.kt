package com.example.prova.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tabela_tarefa",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoriaID"])] // Resolve o aviso de performance
)
data class Tarefa(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descricao: String,
    val categoriaID: Long,
    val prioridade: String,
    val status: String,
    val limitDate: String,
    val isDaily: Boolean = false,
    val createdAt: Long
)