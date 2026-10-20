package com.rojas.ayudacampus.ui.components.graficos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Un dato para GraficoDona o GraficoBarras
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float,
    val color: Color
)

@Composable
fun GraficoDona(datos: List<DatoGrafico>, modifier: Modifier = Modifier) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()
    val colorVacio = MaterialTheme.colorScheme.outlineVariant

    val animProgress = remember(datos) { Animatable(0f) }
    LaunchedEffect(datos) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        // Dona con el total al centro
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val grosor = 22.dp.toPx()
                val esquina = Offset(grosor / 2, grosor / 2)
                val tamano = Size(size.width - grosor, size.height - grosor)
                if (total == 0f) {
                    drawArc(
                        color = colorVacio, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                        topLeft = esquina, size = tamano, style = Stroke(width = grosor)
                    )
                } else {
                    var angulo = -90f
                    val progress = animProgress.value
                    datos.forEach { d ->
                        val barrido = (d.valor / total * 360f) * progress
                        drawArc(
                            color = d.color, startAngle = angulo, sweepAngle = barrido, useCenter = false,
                            topLeft = esquina, size = tamano, style = Stroke(width = grosor)
                        )
                        angulo += (d.valor / total * 360f) * progress
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(total * animProgress.value).toInt()}", style = MaterialTheme.typography.titleLarge)
                Text("tickets", style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.width(20.dp))
        // Leyenda: color, nombre y cantidad a la derecha
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            datos.forEach { d ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(d.color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        d.etiqueta,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${d.valor.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
