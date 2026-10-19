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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TicketDetalleViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    // Flow: si Soporte responde o cambia el estado, la pantalla se actualiza sola
    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Estrellas elegidas en pantalla (0 = todavía no califica)
    private val _calificacion = MutableStateFlow(0)
    val calificacion: StateFlow<Int> = _calificacion

    private fun estadoActual(): String? = ticket.value?.ticket?.estado

    fun cambiarCalificacion(estrellas: Int) {
        _calificacion.value = estrellas
    }

    // No hace nada si el texto está vacío o el ticket está CERRADO
    fun comentar(texto: String) {
        if (texto.isBlank()) return
        if (estadoActual() == null || estadoActual() == EstadoTicket.CERRADO.name) return
        viewModelScope.launch {
            repo.agregarComentario(ticketId, usuario, texto.trim())
        }
    }

    // Solo si está RESUELTO y la calificación es de 1 a 5
    fun cerrar(calificacion: Int, comentarioFinal: String = "") {
        if (estadoActual() != EstadoTicket.RESUELTO.name) return
        if (calificacion !in 1..5) return
        viewModelScope.launch {
            if (comentarioFinal.isNotBlank()) {
                repo.agregarComentario(ticketId, usuario, comentarioFinal.trim())
            }
            repo.cerrarConCalificacion(ticketId, calificacion)
            _calificacion.value = 0
        }
    }

    // Solo si está RESUELTO: vuelve a ABIERTO y se borra la calificación
    fun reabrir() {
        if (estadoActual() != EstadoTicket.RESUELTO.name) return
        viewModelScope.launch {
            repo.reabrir(ticketId)
            _calificacion.value = 0
        }
    }
}
