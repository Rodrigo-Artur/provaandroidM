package com.example.prova.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.prova.data.entity.Categoria
import com.example.prova.data.entity.Tarefa
import com.example.prova.data.dao.CategoriaDao
import com.example.prova.data.dao.TarefaDao

@Database(entities = [Tarefa::class, Categoria::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tarefaDao(): TarefaDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taskflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}