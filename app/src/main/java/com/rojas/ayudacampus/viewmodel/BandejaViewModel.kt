package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.Prioridad
import com.rojas.ayudacampus.model.TicketConSla
import com.rojas.ayudacampus.model.calcularSla
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

data class FiltrosBandeja(
    val estado: String = "TODOS",
    val prioridad: Prioridad? = null,
    val categoriaId: Int? = null,
    val busqueda: String = ""
) {
    fun hayFiltrosActivos(): Boolean =
        estado != "TODOS" || prioridad != null || categoriaId != null || busqueda.isNotBlank()
}

class BandejaViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosBandeja())
    val filtros: StateFlow<FiltrosBandeja> = _filtros

    // Lista de categorías para el filtro
    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo que emite el tiempo actual cada minuto para actualizar los contadores SLA
    private val relojFlow: Flow<Long> = flow {
        emit(System.currentTimeMillis())
        while (true) {
            delay(60_000L)
            emit(System.currentTimeMillis())
        }
    }

    // El DAO ya entrega ALTA, MEDIA, BAJA y fecha descendente; aquí se filtran por estado, prioridad, categoría y texto
    val tickets: StateFlow<List<TicketConSla>> =
        combine(repo.observarTodosLosTickets(), _filtros, relojFlow) { lista, f, ahora ->
            val buscado = f.busqueda.trim()
            val filtrada = lista.filter { item ->
                val t = item.ticket
                val pasaEstado = f.estado == "TODOS" || t.estado == f.estado
                val pasaPrioridad = f.prioridad == null || t.prioridad == f.prioridad.name
                val pasaCategoria = f.categoriaId == null || t.categoriaId == f.categoriaId
                val pasaTexto = if (buscado.isEmpty()) {
                    true
                } else {
                    val tituloCoincide = t.titulo.contains(buscado, ignoreCase = true)
                    val numeroCoincide = buscado.removePrefix("#").toIntOrNull() == t.id
                    val clienteCoincide = item.cliente?.nombre?.contains(buscado, ignoreCase = true) == true
                    tituloCoincide || numeroCoincide || clienteCoincide
                }
                pasaEstado && pasaPrioridad && pasaCategoria && pasaTexto
            }

            filtrada.map { item ->
                TicketConSla(item = item, sla = calcularSla(item, ahora))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarEstado(f: String) {
        _filtros.value = _filtros.value.copy(estado = f)
    }

    fun cambiarFiltro(f: String) = cambiarEstado(f)

    fun cambiarPrioridad(p: Prioridad?) {
        _filtros.value = _filtros.value.copy(prioridad = p)
    }

    fun cambiarCategoria(catId: Int?) {
        _filtros.value = _filtros.value.copy(categoriaId = catId)
    }

    fun cambiarBusqueda(texto: String) {
        _filtros.value = _filtros.value.copy(busqueda = texto)
    }

    fun limpiarFiltros() {
        _filtros.value = FiltrosBandeja()
    }
}
