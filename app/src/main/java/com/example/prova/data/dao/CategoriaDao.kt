package com.example.prova.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prova.data.entity.Categoria

@Dao
interface CategoriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: Categoria)

    @Delete
    suspend fun deleteCategoria(categoria: Categoria)

    @Query("SELECT * FROM tabela_categorias ORDER BY nome ASC")
    fun getAllCategorias(): LiveData<List<Categoria>>
}