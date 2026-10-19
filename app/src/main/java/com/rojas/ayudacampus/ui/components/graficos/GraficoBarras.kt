package com.rojas.ayudacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(datos: List<DatoGrafico>, modifier: Modifier = Modifier) {
    val maximo = (datos.maxOfOrNull { it.valor } ?: 0f).coerceAtLeast(1f)
    val colorEje = MaterialTheme.colorScheme.outline

    Column(modifier) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            if (datos.isEmpty()) return@Canvas
            val espacio = size.width / datos.size
            val ancho = espacio * 0.55f
            datos.forEachIndexed { i, d ->
                val alto = d.valor / maximo * (size.height - 4.dp.toPx())
                drawRoundRect(
                    color = d.color,
                    topLeft = Offset(i * espacio + (espacio - ancho) / 2, size.height - alto),
                    size = Size(ancho, alto),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
            }
            drawLine(
                color = colorEje,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            datos.forEach { d ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${d.valor.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        d.etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
