package com.rojas.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comentarios",
    foreignKeys = [
        ForeignKey(
            entity = Ticket::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ticketId")]
)
data class Comentario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: Int,
    val autorId: Int,
    val autorNombre: String, // "foto" del autor en ese momento
    val autorRol: String,
    val texto: String,
    val fecha: Long = System.currentTimeMillis(),
    val interno: Boolean = false
)
