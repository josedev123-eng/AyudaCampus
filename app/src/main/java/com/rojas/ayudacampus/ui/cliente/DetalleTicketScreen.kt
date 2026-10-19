package com.rojas.ayudacampus.ui.cliente

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.ui.components.AyudaButton
import com.rojas.ayudacampus.ui.components.BurbujaComentario
import com.rojas.ayudacampus.ui.components.CampoMensaje
import com.rojas.ayudacampus.ui.components.EstadoChip
import com.rojas.ayudacampus.ui.components.Estrellas
import com.rojas.ayudacampus.ui.components.PrioridadChip
import com.rojas.ayudacampus.ui.components.StepperTicket
import com.rojas.ayudacampus.ui.components.formatearFecha
import com.rojas.ayudacampus.ui.components.iconoCategoria
import com.rojas.ayudacampus.ui.components.numeroTicket
import com.rojas.ayudacampus.ui.components.tiempoRelativo
import com.rojas.ayudacampus.ui.theme.EstadoResuelto
import com.rojas.ayudacampus.viewmodel.TicketDetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTicketScreen(
    vm: TicketDetalleViewModel,
    usuario: Usuario,
    onAtras: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()
    val calificacion by vm.calificacion.collectAsState()
    var texto by remember { mutableStateOf("") }
    var comentarioFinal by remember { mutableStateOf("") }

    val d = detalle
    val estado = d?.ticket?.estado
    val resuelto = estado == EstadoTicket.RESUELTO.name
    val cerrado = estado == EstadoTicket.CERRADO.name

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (d != null) "Ticket ${numeroTicket(d.ticket.id)}" else "Ticket",
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
        },
        bottomBar = {
            // El campo para escribir solo aparece si el ticket sigue abierto o en proceso
            if (d != null && !resuelto && !cerrado) {
                CampoMensaje(
                    texto = texto,
                    onCambio = { texto = it },
                    onEnviar = {
                        vm.comentar(texto)
                        texto = ""
                    }
                )
            }
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
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                item { CabeceraTicket(d) }

                item { StepperTicket(d.ticket.estado) }

                // Aviso verde cuando Soporte lo marcó como resuelto
                if (resuelto) {
                    item { AvisoResuelto() }
                }

                item {
                    Text(
                        "Conversación",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                if (d.comentarios.isEmpty()) {
                    item {
                        Text(
                            "Aún no hay comentarios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(d.comentarios.sortedBy { it.fecha }, key = { it.id }) { c ->
                    // Los míos a la derecha
                    BurbujaComentario(c, esPropio = c.autorId == usuario.id)
                }

                // Calificar y cerrar, o reabrir
                if (resuelto) {
                    item {
                        TarjetaCalificar(
                            calificacion = calificacion,
                            onCalificar = { vm.cambiarCalificacion(it) }
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = comentarioFinal,
                            onValueChange = { comentarioFinal = it },
                            label = { Text("Comentario final (opcional)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        // Deshabilitado mientras la calificación sea 0
                        AyudaButton(
                            texto = "Confirmar y cerrar",
                            enabled = calificacion > 0,
                            onClick = {
                                vm.cerrar(calificacion, comentarioFinal)
                                comentarioFinal = ""
                            }
                        )
                    }
                    item {
                        OutlinedButton(
                            onClick = { vm.reabrir() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Reabrir ticket", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Ticket cerrado: muestra la calificación recibida
                if (cerrado) {
                    item { TarjetaCerrado(d.ticket.calificacion ?: 0, d.ticket.fechaCierre) }
                }
            }
        }
    }
}

// Título, chips, categoría, ubicación y tiempo
@Composable
private fun CabeceraTicket(d: TicketConDetalle) {
    val t = d.ticket
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(t.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EstadoChip(t.estado)
                PrioridadChip(t.prioridad)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    iconoCategoria(d.categoria?.icono ?: "tag"),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                val lugar = if (t.ubicacion.isBlank()) "" else " · ${t.ubicacion}"
                Text(
                    "${d.categoria?.nombre ?: "-"}$lugar · ${tiempoRelativo(t.fecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(t.descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AvisoResuelto() {
    Card(
        colors = CardDefaults.cardColors(containerColor = EstadoResuelto.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = EstadoResuelto)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Soporte marcó este ticket como resuelto.",
                    style = MaterialTheme.typography.titleSmall,
                    color = EstadoResuelto
                )
                Text(
                    "¿El problema se solucionó?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TarjetaCalificar(calificacion: Int, onCalificar: (Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Califica la atención", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Estrellas(valor = calificacion, onCambio = onCalificar)
            Text(
                "$calificacion de 5 estrellas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaCerrado(calificacion: Int, fechaCierre: Long?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Ticket cerrado", style = MaterialTheme.typography.titleSmall)
            if (fechaCierre != null) {
                Text(
                    "Cerrado el ${formatearFecha(fechaCierre)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Estrellas(valor = calificacion, tamano = 28.dp)
            Text(
                "Calificación: $calificacion de 5",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
