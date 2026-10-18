package com.rojas.ayudacampus

import android.app.Application
import com.rojas.ayudacampus.data.local.AyudaDatabase
import com.rojas.ayudacampus.data.local.DatosIniciales
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.data.repository.AyudaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AyudaApp : Application() {

    lateinit var database: AyudaDatabase
    lateinit var repository: AyudaRepository

    override fun onCreate() {
        super.onCreate()
        database = AyudaDatabase.obtener(this)
        repository = AyudaRepositoryRoom(database.categoriaDao())

        // Siembra los datos de prueba solo la primera vez
        CoroutineScope(Dispatchers.IO).launch {
            DatosIniciales.sembrar(database)
        }
    }
}
