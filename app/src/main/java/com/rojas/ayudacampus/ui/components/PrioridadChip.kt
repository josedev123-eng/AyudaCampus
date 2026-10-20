package com.rojas.ayudacampus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.Prioridad
import com.rojas.ayudacampus.ui.theme.PrioridadAlta
import com.rojas.ayudacampus.ui.theme.PrioridadBaja
import com.rojas.ayudacampus.ui.theme.PrioridadMedia

fun colorPrioridad(prioridad: String): Color = when (prioridad) {
    Prioridad.ALTA.name -> PrioridadAlta
    Prioridad.MEDIA.name -> PrioridadMedia
    else -> PrioridadBaja
}

@Composable
fun PrioridadChip(prioridad: String) {
    val targetColor = colorPrioridad(prioridad)
    val color by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "colorPrioridad"
    )
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = Prioridad.valueOf(prioridad).etiqueta,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
