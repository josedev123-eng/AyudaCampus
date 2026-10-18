package com.rojas.ayudacampus.data.repository

import com.rojas.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

// El contrato: los ViewModels solo conocen esta interface
interface AyudaRepository {

    // Usuarios
    // TODO (Paso 4): suspend fun login(correo: String, clave: String): Usuario?

    // Categorías (ejemplo resuelto)
    fun observarCategorias(): Flow<List<Categoria>>
    fun observarCategoriasActivas(): Flow<List<Categoria>>
    suspend fun guardarCategoria(c: Categoria)
    suspend fun eliminarCategoria(c: Categoria)
    suspend fun cambiarActiva(id: Int, activa: Boolean)

    // Tickets
    // TODO (Paso 4): crearTicket, observarTicketsCliente, observarTodosLosTickets,
    //   observarTicket, cambiarEstado, cambiarPrioridad, cerrarConCalificacion,
    //   reabrir y agregarComentario

    // Panel
    // TODO (Paso 4): observarCantidadPorEstado, observarCantidadPorCategoria,
    //   observarAltasSinAtender
}