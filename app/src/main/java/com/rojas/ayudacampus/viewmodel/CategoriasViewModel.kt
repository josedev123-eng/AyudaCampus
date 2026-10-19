package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.ConteoCategoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriasViewModel(private val repo: AyudaRepository) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cuántos tickets tiene cada categoría (para la vista previa del formulario)
    val conteos: StateFlow<List<ConteoCategoria>> = repo.observarCantidadPorCategoria()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun alternar(c: Categoria) {
        viewModelScope.launch { repo.cambiarActiva(c.id, !c.activa) }
    }

    // Sirve para crear (id = 0) y para editar. Devuelve false si el nombre está vacío
    fun guardar(categoria: Categoria): Boolean {
        if (categoria.nombre.isBlank()) {
            _mensaje.value = "El nombre es obligatorio"
            return false
        }
        viewModelScope.launch {
            repo.guardarCategoria(categoria.copy(nombre = categoria.nombre.trim()))
        }
        return true
    }

    // No se puede eliminar si ya tiene tickets: la clave foránea lo impide
    fun eliminar(categoria: Categoria, alTerminar: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                repo.eliminarCategoria(categoria)
                alTerminar(true)
            } catch (e: Exception) {
                _mensaje.value = "No se puede eliminar: la categoría ya tiene tickets"
                alTerminar(false)
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}