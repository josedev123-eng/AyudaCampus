package com.rojas.ayudacampus.data.local

import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.data.local.entity.Comentario
import com.rojas.ayudacampus.data.local.entity.Ticket
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.model.Prioridad
import com.rojas.ayudacampus.model.Rol

// Datos de prueba (ficticios, Ley 29733). Solo se cargan la primera vez.
object DatosIniciales {

    suspend fun sembrar(db: AyudaDatabase) {
        // Si ya hay usuarios, la base ya fue sembrada
        if (db.usuarioDao().contar() != 0) return

        // 5 categorías -> quedan con id 1 a 5 en este orden
        db.categoriaDao().insertarTodas(
            listOf(
                Categoria(nombre = "Hardware", icono = "pc"),
                Categoria(nombre = "Software", icono = "code"),
                Categoria(nombre = "Red / Wi-Fi", icono = "wifi"),
                Categoria(nombre = "Cuenta", icono = "key"),
                Categoria(nombre = "Impresión", icono = "printer")
            )
        )

        // 2 usuarios -> Juan queda con id 1 y Soporte con id 2
        db.usuarioDao().insertarTodos(
            listOf(
                Usuario(nombre = "Juan Pérez", correo = "juan@campus.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Ana Soporte", correo = "soporte@campus.test", clave = "admin", rol = Rol.ADMIN.name)
            )
        )

        val ahora = System.currentTimeMillis()
        val hora = 3600 * 1000L
        val dao = db.ticketDao()

        // 6 tickets en distintos estados y prioridades
        dao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 3,
                titulo = "Wi-Fi no conecta en el laboratorio 3",
                descripcion = "Las computadoras del laboratorio 3 no se conectan a la red del campus desde la mañana.",
                ubicacion = "Pabellón B - Lab 3",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 3 * hora
            )
        )
        val ticketProyector = dao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 1,
                titulo = "Proyector del aula 205 no enciende",
                descripcion = "El proyector se queda con la luz naranja y no muestra imagen.",
                ubicacion = "Aula 205",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = ahora - 5 * hora
            )
        ).toInt()
        val ticketCuenta = dao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 4,
                titulo = "No puedo entrar al campus virtual",
                descripcion = "Me sale que mi cuenta está bloqueada después de cambiar la contraseña.",
                ubicacion = "Remoto",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = ahora - 26 * hora
            )
        ).toInt()
        val ticketSoftware = dao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 2,
                titulo = "Instalar Android Studio en el lab 4",
                descripcion = "Necesitamos Android Studio actualizado para el curso de móviles.",
                ubicacion = "Pabellón B - Lab 4",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.RESUELTO.name,
                fecha = ahora - 48 * hora
            )
        ).toInt()
        dao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 5,
                titulo = "Impresora de biblioteca atasca papel",
                descripcion = "La impresora del segundo piso de la biblioteca atasca cada hoja.",
                ubicacion = "Biblioteca - 2do piso",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.CERRADO.name,
                fecha = ahora - 72 * hora,
                fechaCierre = ahora - 60 * hora,
                calificacion = 5
            )
        )
        dao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 1,
                titulo = "Teclado con teclas que no responden",
                descripcion = "En la PC 12 del lab 1 no funcionan las teclas A, S y D.",
                ubicacion = "Pabellón A - Lab 1",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - hora / 2
            )
        )

        // 4 comentarios de ejemplo
        dao.insertarComentario(
            Comentario(
                ticketId = ticketProyector, autorId = 1, autorNombre = "Juan Pérez", autorRol = Rol.CLIENTE.name,
                texto = "Hoy tenemos exposición a las 4 pm, ¿lo podrán revisar antes?",
                fecha = ahora - 4 * hora
            )
        )
        dao.insertarComentario(
            Comentario(
                ticketId = ticketProyector, autorId = 2, autorNombre = "Ana Soporte", autorRol = Rol.ADMIN.name,
                texto = "Ya vamos en camino con un proyector de reemplazo.",
                fecha = ahora - 3 * hora
            )
        )
        // Nota interna de ejemplo
        dao.insertarComentario(
            Comentario(
                ticketId = ticketProyector, autorId = 2, autorNombre = "Ana Soporte", autorRol = Rol.ADMIN.name,
                texto = "Revisar garantía del proyector con el proveedor",
                fecha = ahora - 2 * hora,
                interno = true
            )
        )
        dao.insertarComentario(
            Comentario(
                ticketId = ticketCuenta, autorId = 2, autorNombre = "Ana Soporte", autorRol = Rol.ADMIN.name,
                texto = "Estamos desbloqueando tu cuenta, te avisamos en unos minutos.",
                fecha = ahora - 20 * hora
            )
        )
        dao.insertarComentario(
            Comentario(
                ticketId = ticketSoftware, autorId = 2, autorNombre = "Ana Soporte", autorRol = Rol.ADMIN.name,
                texto = "Listo, Android Studio quedó instalado en las 20 PCs.",
                fecha = ahora - 30 * hora
            )
        )
    }
}
