package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AtenderViewModel(
    private val repo: AyudaRepository,
    private val ticketId: Int,
    private val usuario: Usuario
) : ViewModel() {

    val ticket: StateFlow<TicketConDetalle?> = repo.observarTicket(ticketId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Lo que Soporte elige en pantalla; se guarda con "Guardar cambios"
    private val _estadoElegido = MutableStateFlow<EstadoTicket?>(null)
    val estadoElegido: StateFlow<EstadoTicket?> = _estadoElegido

    private val _prioridadElegida = MutableStateFlow<Prioridad?>(null)
    val prioridadElegida: StateFlow<Prioridad?> = _prioridadElegida

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private fun estaCerrado(): Boolean =
        ticket.value?.ticket?.estado == EstadoTicket.CERRADO.name

    // Soporte solo puede elegir ABIERTO, EN_PROCESO o RESUELTO
    fun cambiarEstado(estado: EstadoTicket) {
        if (estado == EstadoTicket.CERRADO) {
            _mensaje.value = "Solo el estudiante puede cerrar el ticket"
            return
        }
        if (estaCerrado()) return
        _estadoElegido.value = estado
    }

    fun cambiarPrioridad(p: Prioridad) {
        if (estaCerrado()) return
        _prioridadElegida.value = p
    }

    // La respuesta se envía al momento (no espera a "Guardar cambios")
    fun responder(texto: String) {
        if (texto.isBlank() || estaCerrado()) return
        viewModelScope.launch {
            repo.agregarComentario(ticketId, usuario, texto.trim())
        }
    }

    fun guardar() {
        if (ticket.value == null) return
        if (estaCerrado()) {
            _mensaje.value = "El ticket está cerrado, no se puede modificar"
            return
        }
        viewModelScope.launch {
            _estadoElegido.value?.let { repo.cambiarEstado(ticketId, it) }
            _prioridadElegida.value?.let { repo.cambiarPrioridad(ticketId, it) }
            _mensaje.value = "Cambios guardados"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
