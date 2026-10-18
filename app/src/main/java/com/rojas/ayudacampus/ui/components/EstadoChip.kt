package com.rojas.ayudacampus.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.EstadoTicket
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.EstadoCerrado
import com.rojas.ayudacampus.ui.theme.EstadoEnProceso
import com.rojas.ayudacampus.ui.theme.EstadoResuelto

fun colorEstado(estado: String): Color = when (estado) {
    EstadoTicket.ABIERTO.name -> EstadoAbierto
    EstadoTicket.EN_PROCESO.name -> EstadoEnProceso
    EstadoTicket.RESUELTO.name -> EstadoResuelto
    else -> EstadoCerrado
}

@Composable
fun EstadoChip(estado: String) {
    val color = colorEstado(estado)
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = EstadoTicket.valueOf(estado).etiqueta,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
