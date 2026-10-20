package com.rojas.ayudacampus.data.repository

import com.rojas.ayudacampus.data.local.dao.CategoriaDao
import com.rojas.ayudacampus.data.local.dao.TicketDao
import com.rojas.ayudacampus.data.local.dao.UsuarioDao
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.data.local.entity.Comentario
import com.rojas.ayudacampus.data.local.entity.Ticket
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.model.ConteoCategoria
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.Flow
import com.rojas.ayudacampus.model.Rol

class AyudaRepositoryRoom(
    private val categoriaDao: CategoriaDao,
    private val usuarioDao: UsuarioDao,
    private val ticketDao: TicketDao
) : AyudaRepository {

    // ---------- Usuarios ----------

    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarioDao.login(correo, clave)

    override suspend fun registrar(nombre: String, correo: String, clave: String): Boolean {
        val correoLimpio = correo.trim().lowercase()
        if (usuarioDao.existeCorreo(correoLimpio) > 0) {
            return false
        }
        val nuevoUsuario = Usuario(
            nombre = nombre.trim(),
            correo = correoLimpio,
            clave = clave,
            rol = Rol.CLIENTE.name
        )
        usuarioDao.insertar(nuevoUsuario)
        return true
    }

    override suspend fun obtenerUsuario(id: Int): Usuario? =
        usuarioDao.buscarPorId(id)

    override suspend fun obtenerUsuarioPorRol(rol: Rol): Usuario? =
        usuarioDao.buscarPorRol(rol.name)

    // ---------- Categorías (lista) ----------

    override fun observarCategorias(): Flow<List<Categoria>> = categoriaDao.observarTodas()

    override fun observarCategoriasActivas(): Flow<List<Categoria>> = categoriaDao.observarActivas()

    override suspend fun guardarCategoria(c: Categoria) {
        // id 0 = nueva; si ya tiene id se actualiza
        if (c.id == 0) {
            categoriaDao.insertar(c)
        } else {
            categoriaDao.actualizar(c)
        }
    }

    override suspend fun eliminarCategoria(c: Categoria) = categoriaDao.eliminar(c)

    override suspend fun cambiarActiva(id: Int, activa: Boolean) = categoriaDao.cambiarActiva(id, activa)

    // ---------- Tickets ----------

    override suspend fun crearTicket(
        clienteId: Int, categoriaId: Int, titulo: String,
        descripcion: String, ubicacion: String, prioridad: Prioridad
    ): Long {
        val ticket = Ticket(
            clienteId = clienteId,
            categoriaId = categoriaId,
            titulo = titulo,
            descripcion = descripcion,
            ubicacion = ubicacion,
            prioridad = prioridad.name,
            estado = EstadoTicket.ABIERTO.name,
            fecha = System.currentTimeMillis()
        )
        return ticketDao.insertarTicket(ticket)
    }

    override fun observarTicketsCliente(clienteId: Int): Flow<List<TicketConDetalle>> =
        ticketDao.observarPorCliente(clienteId)

    override fun observarTodosLosTickets(): Flow<List<TicketConDetalle>> =
        ticketDao.observarTodos()

    override fun observarTicket(id: Int): Flow<TicketConDetalle?> =
        ticketDao.observarUno(id)

    override suspend fun cambiarEstado(ticketId: Int, estado: EstadoTicket) =
        ticketDao.actualizarEstado(ticketId, estado.name)

    override suspend fun cambiarPrioridad(ticketId: Int, prioridad: Prioridad) =
        ticketDao.actualizarPrioridad(ticketId, prioridad.name)

    override suspend fun cerrarConCalificacion(ticketId: Int, calificacion: Int) =
        ticketDao.cerrar(ticketId, calificacion, System.currentTimeMillis())

    override suspend fun reabrir(ticketId: Int) = ticketDao.reabrir(ticketId)

    override suspend fun agregarComentario(ticketId: Int, autor: Usuario, texto: String, interno: Boolean) {
        // Copia nombre y rol del autor en ese momento
        val comentario = Comentario(
            ticketId = ticketId,
            autorId = autor.id,
            autorNombre = autor.nombre,
            autorRol = autor.rol,
            texto = texto,
            fecha = System.currentTimeMillis(),
            interno = interno
        )
        ticketDao.insertarComentario(comentario)
    }

    // ---------- Panel ----------

    override fun observarCantidadPorEstado(estado: EstadoTicket): Flow<Int> =
        ticketDao.contarPorEstado(estado.name)

    override fun observarCantidadPorCategoria(): Flow<List<ConteoCategoria>> =
        ticketDao.contarPorCategoria()

    override fun observarAltasSinAtender(limite: Long): Flow<Int> =
        ticketDao.contarAltasSinAtender(limite)

    override fun observarTiempoPromedioResolucion(): Flow<Long?> =
        ticketDao.observarTiempoPromedioResolucion()
}
