package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BandejaViewModel(private val repo: AyudaRepository) : ViewModel() {

    // "TODOS", "ABIERTO", "EN_PROCESO" o "RESUELTO"
    private val _filtro = MutableStateFlow("TODOS")
    val filtro: StateFlow<String> = _filtro

    // El DAO ya entrega ALTA, MEDIA, BAJA y fecha descendente; aquí solo se filtra
    // (filter conserva el orden)
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(repo.observarTodosLosTickets(), _filtro) { lista, filtro ->
            if (filtro == "TODOS") lista else lista.filter { it.ticket.estado == filtro }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarFiltro(f: String) {
        _filtro.value = f
    }
}
