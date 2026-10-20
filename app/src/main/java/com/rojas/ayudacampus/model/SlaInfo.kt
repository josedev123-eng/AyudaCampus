package com.rojas.ayudacampus.model

import com.rojas.ayudacampus.data.local.relation.TicketConDetalle

enum class SlaEstado {
    NORMAL,
    AMBAR,
    ROJO
}

data class SlaInfo(
    val texto: String,
    val estado: SlaEstado
)

data class TicketConSla(
    val item: TicketConDetalle,
    val sla: SlaInfo?
)

fun calcularSla(item: TicketConDetalle, ahora: Long): SlaInfo? {
    val t = item.ticket
    // Solo aplica a tickets ABIERTO o EN_PROCESO
    if (t.estado != EstadoTicket.ABIERTO.name && t.estado != EstadoTicket.EN_PROCESO.name) {
        return null
    }

    val duracionHoras = when (t.prioridad) {
        Prioridad.ALTA.name -> 4L
        Prioridad.MEDIA.name -> 24L
        Prioridad.BAJA.name -> 72L
        else -> 24L
    }

    val duracionTotalMs = duracionHoras * 3600 * 1000L
    val tiempoLimite = t.fecha + duracionTotalMs
    val tiempoRestante = tiempoLimite - ahora

    return if (tiempoRestante > 0) {
        val porcentajeRestante = (tiempoRestante.toDouble() / duracionTotalMs) * 100.0
        val minTotales = tiempoRestante / 60000L
        val horas = minTotales / 60
        val minutos = minTotales % 60

        val texto = when {
            horas > 0 && minutos > 0 -> "Quedan $horas h $minutos min"
            horas > 0 -> "Quedan $horas h"
            minutos > 0 -> "Quedan $minutos min"
            else -> "Quedan < 1 min"
        }

        val estado = if (porcentajeRestante < 25.0) SlaEstado.AMBAR else SlaEstado.NORMAL
        SlaInfo(texto = texto, estado = estado)
    } else {
        val tiempoVencido = ahora - tiempoLimite
        val minVencidos = tiempoVencido / 60000L
        val horas = minVencidos / 60
        val minutos = minVencidos % 60

        val texto = when {
            horas > 0 && minutos > 0 -> "Vencido hace $horas h $minutos min"
            horas > 0 -> "Vencido hace $horas h"
            minutos > 0 -> "Vencido hace $minutos min"
            else -> "Vencido hace < 1 min"
        }

        SlaInfo(texto = texto, estado = SlaEstado.ROJO)
    }
}
