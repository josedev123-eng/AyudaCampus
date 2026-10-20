package com.rojas.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.relation.TicketConDetalle
import com.rojas.ayudacampus.model.SlaEstado
import com.rojas.ayudacampus.model.SlaInfo
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.PrioridadAlta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Franja vertical de 4.dp a la izquierda con el color de la prioridad + icono de la categoría + chips
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketCard(
    item: TicketConDetalle,
    onClick: () -> Unit,
    mostrarCliente: Boolean = false,
    sla: SlaInfo? = null,
    modifier: Modifier = Modifier
) {
    val t = item.ticket
    // Estudiante: ubicación (o categoría). Soporte: nombre corto del cliente
    val detalle = if (mostrarCliente) {
        nombreCorto(item.cliente?.nombre ?: "-")
    } else {
        t.ubicacion.ifBlank { item.categoria?.nombre ?: "-" }
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Franja vertical de 4.dp a la izquierda con el color de la prioridad
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(colorPrioridad(t.prioridad))
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // Icono de la categoría
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        iconoCategoria(item.categoria?.icono ?: "tag"),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        t.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    // #0012 · Lab 3   o   #0012 · Juan P.  + tiempo a la derecha
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${numeroTicket(t.id)} · $detalle",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            tiempoRelativo(t.fecha),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    // Chips a la izquierda
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EstadoChip(t.estado)
                        PrioridadChip(t.prioridad)
                        sla?.let { SlaChip(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun SlaChip(sla: SlaInfo) {
    val (colorFondo, colorTexto) = when (sla.estado) {
        SlaEstado.NORMAL -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        SlaEstado.AMBAR -> EstadoAbierto.copy(alpha = 0.15f) to EstadoAbierto
        SlaEstado.ROJO -> PrioridadAlta.copy(alpha = 0.15f) to PrioridadAlta
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colorFondo)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = sla.texto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto
        )
    }
}

// Lista de iconos que puede tener una categoría
val ICONOS_CATEGORIA = listOf("pc", "code", "wifi", "key", "printer", "tag")

// Devuelve el icono según el texto guardado en la categoría
fun iconoCategoria(icono: String): ImageVector = when (icono) {
    "pc" -> Icons.Filled.Computer
    "code" -> Icons.Filled.Code
    "wifi" -> Icons.Filled.Wifi
    "key" -> Icons.Filled.Key
    "printer" -> Icons.Filled.Print
    else -> Icons.Filled.LocalOffer
}

// Convierte la fecha en milisegundos a texto, por ejemplo 20/10/2026 21:30
fun formatearFecha(millis: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PE"))
    return formato.format(Date(millis))
}

// "Juan Pérez" -> "Juan P."
fun nombreCorto(nombre: String): String {
    val partes = nombre.trim().split(" ")
    return if (partes.size > 1) "${partes[0]} ${partes[1].first()}." else nombre
}

// 12 -> "#0012"
fun numeroTicket(id: Int): String = "#" + id.toString().padStart(4, '0')

fun tiempoRelativo(millis: Long): String {
    val minutos = (System.currentTimeMillis() - millis) / 60000
    val horas = minutos / 60
    val dias = horas / 24
    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace $minutos min"
        horas < 24 -> "Hace $horas h"
        dias < 2 -> "Ayer"
        else -> "Hace $dias d"
    }
}
