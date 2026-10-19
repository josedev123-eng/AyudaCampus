package com.rojas.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.model.Prioridad
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.components.AyudaButton
import com.rojas.ayudacampus.ui.components.BurbujaComentario
import com.rojas.ayudacampus.ui.components.CampoMensaje
import com.rojas.ayudacampus.ui.components.colorEstado
import com.rojas.ayudacampus.ui.components.colorPrioridad
import com.rojas.ayudacampus.ui.components.numeroTicket
import com.rojas.ayudacampus.viewmodel.AtenderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtenderTicketScreen(
    vm: AtenderViewModel,
    onAtras: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()
    val estadoElegido by vm.estadoElegido.collectAsState()
    val prioridadElegida by vm.prioridadElegida.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    var texto by remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }

    // Muestra "Cambios guardados" u otros avisos del ViewModel
    LaunchedEffect(mensaje) {
        val m = mensaje
        if (m != null) {
            snackbar.showSnackbar(m)
            vm.limpiarMensaje()
        }
    }

    val d = detalle

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (d != null) "Atender ticket ${numeroTicket(d.ticket.id)}" else "Atender ticket",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (d == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            val t = d.ticket
            val cerrado = t.estado == EstadoTicket.CERRADO.name
            // Lo elegido en pantalla o, si no se tocó, lo que está guardado
            val estadoActual = estadoElegido ?: EstadoTicket.valueOf(t.estado)
            val prioridadActual = prioridadElegida ?: Prioridad.valueOf(t.prioridad)

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // Datos del ticket y del cliente
                item { DatosTicket(d) }

                // Selector de estado (Cerrado siempre deshabilitado)
                item {
                    Text("Estado", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EstadoTicket.entries.forEach { e ->
                            BotonOpcion(
                                texto = e.etiqueta,
                                elegido = estadoActual == e,
                                color = colorEstado(e.name),
                                habilitado = !cerrado && e != EstadoTicket.CERRADO,
                                onClick = { vm.cambiarEstado(e) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Selector de prioridad
                item {
                    Text("Prioridad", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Prioridad.entries.forEach { p ->
                            BotonOpcion(
                                texto = p.etiqueta,
                                elegido = prioridadActual == p,
                                color = colorPrioridad(p.name),
                                habilitado = !cerrado,
                                onClick = { vm.cambiarPrioridad(p) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Conversación: para Soporte, los mensajes propios son los del rol ADMIN
                item { Text("Conversación", style = MaterialTheme.typography.titleSmall) }
                items(d.comentarios.sortedBy { it.fecha }, key = { it.id }) { c ->
                    BurbujaComentario(c, esPropio = c.autorRol == Rol.ADMIN.name)
                }

                if (cerrado) {
                    item {
                        Text(
                            "El estudiante cerró este ticket; ya no se puede modificar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Campo para responder
                    item {
                        CampoMensaje(
                            texto = texto,
                            onCambio = { texto = it },
                            onEnviar = {
                                vm.responder(texto)
                                texto = ""
                            },
                            placeholder = "Responder al cliente..."
                        )
                    }
                    item {
                        AyudaButton(texto = "Guardar cambios", onClick = { vm.guardar() })
                    }
                }
            }
        }
    }
}

// Título, cliente, ubicación, categoría y descripción
@Composable
private fun DatosTicket(d: TicketConDetalle) {
    val t = d.ticket
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(t.titulo, style = MaterialTheme.typography.titleMedium)
            val lugar = if (t.ubicacion.isBlank()) "" else " · ${t.ubicacion}"
            Text(
                "${d.cliente?.nombre ?: "-"}$lugar · ${d.categoria?.nombre ?: "-"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(t.descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Botón tipo píldora: se pinta con su color cuando está elegido
@Composable
private fun BotonOpcion(
    texto: String,
    elegido: Boolean,
    color: Color,
    habilitado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Surface(
        onClick = onClick,
        enabled = habilitado,
        color = if (elegido) color else MaterialTheme.colorScheme.surface,
        contentColor = when {
            elegido -> Color.White
            habilitado -> MaterialTheme.colorScheme.onSurfaceVariant
            else -> MaterialTheme.colorScheme.outline
        },
        shape = RoundedCornerShape(50),
        border = if (elegido) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}