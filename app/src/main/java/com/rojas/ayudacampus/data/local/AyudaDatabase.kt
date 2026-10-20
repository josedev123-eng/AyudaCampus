package com.rojas.ayudacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rojas.ayudacampus.data.local.dao.CategoriaDao
import com.rojas.ayudacampus.data.local.dao.TicketDao
import com.rojas.ayudacampus.data.local.dao.UsuarioDao
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.data.local.entity.Comentario
import com.rojas.ayudacampus.data.local.entity.Ticket
import com.rojas.ayudacampus.data.local.entity.Usuario

@Database(
    entities = [Categoria::class, Usuario::class, Ticket::class, Comentario::class],
    version = 2,
    exportSchema = false
)
abstract class AyudaDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile private var INSTANCE: AyudaDatabase? = null

        fun obtener(context: Context): AyudaDatabase = INSTANCE ?: synchronized(this) {
            val instancia = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AyudaDatabase::class.java,
                "ayudacampus.db"
            ).fallbackToDestructiveMigration().build()
            INSTANCE = instancia
            instancia
        }
    }
}
