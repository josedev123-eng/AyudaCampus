package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NuevoTicketViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario
) : ViewModel() {

    // Solo categorías activas
    val categorias: StateFlow<List<Categoria>> = repo.observarCategoriasActivas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Estado del formulario ----------

    private val _categoriaId = MutableStateFlow<Int?>(null)
    val categoriaId: StateFlow<Int?> = _categoriaId

    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo

    private val _ubicacion = MutableStateFlow("")
    val ubicacion: StateFlow<String> = _ubicacion

    private val _prioridad = MutableStateFlow(Prioridad.MEDIA) // Media por defecto
    val prioridad: StateFlow<Prioridad> = _prioridad

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    // ---------- Errores por campo ----------

    private val _errorCategoria = MutableStateFlow<String?>(null)
    val errorCategoria: StateFlow<String?> = _errorCategoria

    private val _errorTitulo = MutableStateFlow<String?>(null)
    val errorTitulo: StateFlow<String?> = _errorTitulo

    private val _errorDescripcion = MutableStateFlow<String?>(null)
    val errorDescripcion: StateFlow<String?> = _errorDescripcion

    // Avisa a la pantalla que ya se creó el ticket
    private val _enviado = MutableStateFlow(false)
    val enviado: StateFlow<Boolean> = _enviado

    fun elegirCategoria(id: Int) {
        _categoriaId.value = id
        _errorCategoria.value = null
    }

    fun cambiarTitulo(texto: String) {
        _titulo.value = texto
        _errorTitulo.value = null
    }

    fun cambiarUbicacion(texto: String) {
        _ubicacion.value = texto
    }

    fun cambiarPrioridad(p: Prioridad) {
        _prioridad.value = p
    }

    fun cambiarDescripcion(texto: String) {
        _descripcion.value = texto
        _errorDescripcion.value = null
    }

    fun enviar() {
        val titulo = _titulo.value.trim()
        val descripcion = _descripcion.value.trim()
        val categoria = _categoriaId.value
        var valido = true

        // La categoría debe estar elegida y seguir activa
        if (categoria == null || categorias.value.none { it.id == categoria }) {
            _errorCategoria.value = "Elige una categoría"
            valido = false
        }
        if (titulo.length < 5) {
            _errorTitulo.value = "El título debe tener al menos 5 caracteres"
            valido = false
        }
        if (descripcion.length < 10) {
            _errorDescripcion.value = "La descripción debe tener al menos 10 caracteres"
            valido = false
        }
        if (!valido || categoria == null || _enviado.value) return

        viewModelScope.launch {
            repo.crearTicket(
                clienteId = usuario.id,
                categoriaId = categoria,
                titulo = titulo,
                descripcion = descripcion,
                ubicacion = _ubicacion.value.trim(),
                prioridad = _prioridad.value
            )
            _enviado.value = true
        }
    }
}
