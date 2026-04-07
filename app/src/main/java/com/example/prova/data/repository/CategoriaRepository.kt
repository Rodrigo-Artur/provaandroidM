package com.example.prova.data.repository

import androidx.lifecycle.LiveData
import com.example.prova.data.dao.CategoriaDao
import com.example.prova.data.entity.Categoria

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

    // O Room executa consultas LiveData em uma thread em segundo plano automaticamente
    val allCategorias: LiveData<List<Categoria>> = categoriaDao.getAllCategorias()

    suspend fun insert(categoria: Categoria) {
        categoriaDao.insertCategoria(categoria)
    }

    suspend fun delete(categoria: Categoria) {
        categoriaDao.deleteCategoria(categoria)
    }
}