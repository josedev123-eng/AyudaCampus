package com.rojas.ayudacampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"

    // Grafo del estudiante
    const val GRAFO_CLIENTE = "grafo_cliente"
    const val MIS_TICKETS = "mis_tickets"
    const val NUEVO_TICKET = "nuevo_ticket"
    const val DETALLE = "detalle/{ticketId}"
    const val PERFIL = "perfil"

    // Grafo de soporte
    const val GRAFO_SOPORTE = "grafo_soporte"
    const val PANEL = "panel"
    const val BANDEJA = "bandeja"
    const val ATENDER = "atender/{ticketId}"
    const val CATEGORIAS = "categorias"
    const val FORM_CATEGORIA = "form_categoria?id={id}"
    const val AJUSTES = "ajustes"

    fun detalle(id: Int) = "detalle/$id"
    fun atender(id: Int) = "atender/$id"
    fun formCategoria(id: Int) = "form_categoria?id=$id"

    // Pantallas que muestran la NavigationBar
    val TABS_CLIENTE = listOf(MIS_TICKETS, NUEVO_TICKET, PERFIL)
    val TABS_SOPORTE = listOf(PANEL, BANDEJA, CATEGORIAS, AJUSTES)
}
