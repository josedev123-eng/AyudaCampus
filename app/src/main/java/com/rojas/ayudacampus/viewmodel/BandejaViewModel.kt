package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.repository.AyudaRepository
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

class BandejaViewModel(private val repo: AyudaRepository) : ViewModel() {

    // "TODOS", "ABIERTO", "EN_PROCESO" o "RESUELTO"
    private val _filtro = MutableStateFlow("TODOS")
    val filtro: StateFlow<String> = _filtro

    // Flujo que emite el tiempo actual cada minuto para actualizar los contadores SLA
    private val relojFlow: Flow<Long> = flow {
        emit(System.currentTimeMillis())
        while (true) {
            delay(60_000L)
            emit(System.currentTimeMillis())
        }
    }

    // El DAO ya entrega ALTA, MEDIA, BAJA y fecha descendente; aquí solo se filtra y calcula el SLA
    val tickets: StateFlow<List<TicketConSla>> =
        combine(repo.observarTodosLosTickets(), _filtro, relojFlow) { lista, filtro, ahora ->
            val filtrada = if (filtro == "TODOS") lista else lista.filter { it.ticket.estado == filtro }
            filtrada.map { item ->
                TicketConSla(item = item, sla = calcularSla(item, ahora))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarFiltro(f: String) {
        _filtro.value = f
    }
}
