package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MisTicketsViewModel(
    private val repo: AyudaRepository,
    private val usuario: Usuario
) : ViewModel() {

    // "TODOS", "ABIERTO", "EN_PROCESO" o "RESUELTO"
    private val _filtro = MutableStateFlow("TODOS")
    val filtro: StateFlow<String> = _filtro

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    // El estudiante solo ve SUS tickets
    private val misTickets = repo.observarTicketsCliente(usuario.id)

    // Combina los 3 flujos: lista, filtro y búsqueda
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(misTickets, _filtro, _busqueda) { lista, filtro, texto ->
            val buscado = texto.trim()
            lista.filter { item ->
                val t = item.ticket
                // "Todos" incluye también los CERRADOS
                val pasaFiltro = filtro == "TODOS" || t.estado == filtro
                // Busca por título o por número (#12 o 12)
                val pasaTexto = buscado.isEmpty() ||
                        t.titulo.contains(buscado, ignoreCase = true) ||
                        buscado.removePrefix("#").toIntOrNull() == t.id
                pasaFiltro && pasaTexto
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Para el saludo: cuántos tickets tiene abiertos
    val cantidadAbiertos: StateFlow<Int> = misTickets
        .map { lista -> lista.count { it.ticket.estado == EstadoTicket.ABIERTO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun cambiarFiltro(f: String) {
        _filtro.value = f
    }

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }
}
