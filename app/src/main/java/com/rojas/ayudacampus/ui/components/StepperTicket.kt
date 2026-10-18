package com.rojas.ayudacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.EstadoTicket

// Muestra los 4 estados: Abierto -> En proceso -> Resuelto -> Cerrado
@Composable
fun StepperTicket(estado: String) {
    val pasos = EstadoTicket.entries
    val actual = EstadoTicket.valueOf(estado).ordinal
    val activo = MaterialTheme.colorScheme.primary
    val inactivo = MaterialTheme.colorScheme.outlineVariant

    Row(Modifier.fillMaxWidth()) {
        pasos.forEachIndexed { i, paso ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (i == 0) Color.Transparent else if (i <= actual) activo else inactivo)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (i <= actual) activo else inactivo)
                    ) {
                        if (i < actual) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                "${i + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (i <= actual) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (i == pasos.lastIndex) Color.Transparent else if (i < actual) activo else inactivo)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    paso.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (i <= actual) activo else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
