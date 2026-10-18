package com.rojas.ayudacampus.data.repository

import com.rojas.ayudacampus.data.local.dao.CategoriaDao
import com.rojas.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

class AyudaRepositoryRoom(
    private val categoriaDao: CategoriaDao
    // TODO (Paso 4): usuarioDao y ticketDao
) : AyudaRepository {

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

    // TODO (Paso 4): implementar usuarios, tickets y panel
}
