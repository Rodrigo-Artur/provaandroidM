package com.example.prova.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.prova.data.entity.Categoria

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun getAllCategorias(): Flow<List<Categoria>>

    @Insert
    suspend fun insert(categoria: Categoria)
}