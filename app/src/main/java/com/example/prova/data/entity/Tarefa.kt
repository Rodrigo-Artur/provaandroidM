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
    val prioridade: String, // Era Enum, agora é String (Ex: "Baixa", "Média", "Alta")
    val status: String,     // Era Enum, agora é String (Ex: "Pendente", "Concluída")
    val limitDate: String,
    val createdAt: Long
)