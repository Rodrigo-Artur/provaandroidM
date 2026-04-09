package com.example.prova.data.repository

import androidx.lifecycle.LiveData
import com.example.prova.data.dao.CategoriaDao
import com.example.prova.data.entity.Categoria

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

    val allCategorias: LiveData<List<Categoria>> = categoriaDao.getAllCategorias()

    suspend fun insert(categoria: Categoria) {
        categoriaDao.insertCategoria(categoria)
    }

    // ---> ADICIONE ESTA FUNÇÃO <---
    suspend fun update(categoria: Categoria) {
        categoriaDao.updateCategoria(categoria)
    }

    suspend fun delete(categoria: Categoria) {
        categoriaDao.deleteCategoria(categoria)
    }
}