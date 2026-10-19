package com.rojas.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rojas.ayudacampus.data.repository.AyudaRepository
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.ui.components.graficos.DatoGrafico
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.EstadoCerrado
import com.rojas.ayudacampus.ui.theme.EstadoEnProceso
import com.rojas.ayudacampus.ui.theme.EstadoResuelto
import com.rojas.ayudacampus.ui.theme.VioletaPrimario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PanelViewModel(private val repo: AyudaRepository) : ViewModel() {

    // ---------- Contadores ----------

    val abiertos: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoTicket.ABIERTO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val enProceso: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoTicket.EN_PROCESO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val resueltos: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoTicket.RESUELTO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val cerrados: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoTicket.CERRADO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ---------- Datos para los gráficos ----------

    // GraficoDona: un dato por estado con su color
    val porEstado: StateFlow<List<DatoGrafico>> =
        combine(abiertos, enProceso, resueltos, cerrados) { a, p, r, c ->
            listOf(
                DatoGrafico("Abierto", a.toFloat(), EstadoAbierto),
                DatoGrafico("En proceso", p.toFloat(), EstadoEnProceso),
                DatoGrafico("Resuelto", r.toFloat(), EstadoResuelto),
                DatoGrafico("Cerrado", c.toFloat(), EstadoCerrado)
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // GraficoBarras: una barra por categoría; la que tiene más tickets va más oscura
    val porCategoria: StateFlow<List<DatoGrafico>> = repo.observarCantidadPorCategoria()
        .map { lista ->
            val maximo = lista.maxOfOrNull { it.cantidad } ?: 0
            lista.map { c ->
                val color = if (c.cantidad == maximo && maximo > 0) VioletaPrimario
                else VioletaPrimario.copy(alpha = 0.35f)
                // "Red / Wi-Fi" -> "Red" para que entre debajo de la barra
                DatoGrafico(c.nombre.substringBefore(" /"), c.cantidad.toFloat(), color)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Alerta ----------

    // Tickets ALTA y ABIERTOS con más de 1 hora (límite = ahora − 1 hora)
    val altasSinAtender: StateFlow<Int> =
        repo.observarAltasSinAtender(System.currentTimeMillis() - 60 * 60 * 1000L)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
