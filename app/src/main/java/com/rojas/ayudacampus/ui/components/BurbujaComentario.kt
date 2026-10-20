package com.rojas.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.entity.Comentario
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.theme.CianContenedor
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.VioletaContenedor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Propios a la derecha (color primario), los del otro perfil a la izquierda
@Composable
fun BurbujaComentario(c: Comentario, esPropio: Boolean) {
    // "Tú · 10:32" o "Soporte TI · 10:20"
    val autor = when {
        esPropio && !c.interno -> "Tú"
        c.autorRol == Rol.ADMIN.name -> "Soporte TI"
        else -> c.autorNombre
    }
    // Soporte en violeta suave y estudiante en cian suave
    val colorOtro = if (c.autorRol == Rol.ADMIN.name) VioletaContenedor else CianContenedor

    // Si es interno, fondo ámbar suave (EstadoAbierto con alpha)
    val colorFondo = if (c.interno) EstadoAbierto.copy(alpha = 0.15f) else if (esPropio) MaterialTheme.colorScheme.primary else colorOtro
    val colorContenido = if (c.interno) MaterialTheme.colorScheme.onSurface else if (esPropio) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    val inicial = autor.trim().firstOrNull()?.uppercaseChar() ?: '?'

    Row(
        horizontalArrangement = if (esPropio && !c.interno) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!esPropio || c.interno) {
            AvatarAutor(inicial)
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            color = colorFondo,
            contentColor = colorContenido,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (esPropio && !c.interno) 16.dp else 4.dp,
                bottomEnd = if (esPropio && !c.interno) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (c.interno) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Nota interna · $autor · ${horaComentario(c.fecha)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        "$autor · ${horaComentario(c.fecha)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(c.texto, style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (esPropio && !c.interno) {
            Spacer(Modifier.width(8.dp))
            AvatarAutor(inicial)
        }
    }
}

@Composable
private fun AvatarAutor(inicial: Char) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = inicial.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// Barra inferior para escribir un comentario o una respuesta
@Composable
fun CampoMensaje(
    texto: String,
    onCambio: (String) -> Unit,
    onEnviar: () -> Unit,
    placeholder: String = "Escribe un comentario..."
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = onCambio,
                placeholder = { Text(placeholder) },
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(onClick = onEnviar, enabled = texto.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
            }
        }
    }
}

fun horaComentario(millis: Long): String {
    val hoy = Calendar.getInstance()
    val fecha = Calendar.getInstance().apply { timeInMillis = millis }
    val mismoAnio = hoy.get(Calendar.YEAR) == fecha.get(Calendar.YEAR)
    val diaHoy = hoy.get(Calendar.DAY_OF_YEAR)
    val diaFecha = fecha.get(Calendar.DAY_OF_YEAR)
    return when {
        mismoAnio && diaHoy == diaFecha -> SimpleDateFormat("HH:mm", Locale("es", "PE")).format(Date(millis))
        mismoAnio && diaHoy - diaFecha == 1 -> "ayer"
        else -> SimpleDateFormat("dd/MM", Locale("es", "PE")).format(Date(millis))
    }
}
