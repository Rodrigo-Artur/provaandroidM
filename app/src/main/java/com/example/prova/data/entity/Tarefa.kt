package com.example.prova.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class Tarefa(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descricao: String,
    val categoriaID: Long,
    val prioridade: String,       // "Baixa", "Média", "Alta"
    val status: String,           // "Pendente", "Concluída"
    val limitDate: String,        // "dd/MM/yyyy" ou "Sem prazo"
    val isDaily: Boolean = false, // Tarefa diária
    val createdAt: Long
)