package com.example.prova.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prova.data.entity.Categoria

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM tabela_categoria ORDER BY nome ASC")
    fun getAllCategorias(): LiveData<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: Categoria)

    // ---> ADICIONE ESTA LINHA AQUI <---
    @Update
    suspend fun updateCategoria(categoria: Categoria)

    @Delete
    suspend fun deleteCategoria(categoria: Categoria)
}