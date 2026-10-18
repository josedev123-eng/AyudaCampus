package com.rojas.ayudacampus.model

// Resultado de la consulta con LEFT JOIN y COUNT del panel de soporte
data class ConteoCategoria(
    val nombre: String,
    val icono: String,
    val cantidad: Int
)
