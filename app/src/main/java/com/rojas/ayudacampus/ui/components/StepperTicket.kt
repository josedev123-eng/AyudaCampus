package com.rojas.ayudacampus.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.EstadoTicket

// Muestra los 4 estados con animaciones avanzadas: Abierto -> En proceso -> Resuelto -> Cerrado
@Composable
fun StepperTicket(estado: String) {
    val pasos = EstadoTicket.entries
    val actual = EstadoTicket.valueOf(estado).ordinal
    val activo = MaterialTheme.colorScheme.primary
    val inactivo = MaterialTheme.colorScheme.outlineVariant

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Row(Modifier.fillMaxWidth()) {
        pasos.forEachIndexed { i, paso ->
            val isCompleted = i < actual
            val isCurrent = i == actual
            val isPassedOrCurrent = i <= actual

            val circleColor by animateColorAsState(
                targetValue = if (isPassedOrCurrent) activo else inactivo,
                animationSpec = tween(400),
                label = "circleColor_$i"
            )
            val textColor by animateColorAsState(
                targetValue = if (isPassedOrCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(400),
                label = "textColor_$i"
            )

            val lineLeftProgress by animateFloatAsState(
                targetValue = if (i == 0) 0f else if (i <= actual) 1f else 0f,
                animationSpec = tween(600),
                label = "lineLeft_$i"
            )
            val lineRightProgress by animateFloatAsState(
                targetValue = if (i == pasos.lastIndex) 0f else if (i < actual) 1f else 0f,
                animationSpec = tween(600),
                label = "lineRight_$i"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left line
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                    ) {
                        if (i > 0) {
                            Box(
                                Modifier
                                    .fillMaxWidth(lineLeftProgress)
                                    .height(3.dp)
                                    .background(activo)
                            )
                        }
                    }

                    // Circle
                    val scale = if (isCurrent) pulseScale else 1f
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(circleColor)
                    ) {
                        AnimatedContent(
                            targetState = isCompleted,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) + scaleIn(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ) togetherWith fadeOut(animationSpec = tween(300))
                            },
                            label = "stepIconAnimation_$i"
                        ) { completed ->
                            if (completed) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    "${i + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textColor
                                )
                            }
                        }
                    }

                    // Right line
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                    ) {
                        if (i < pasos.lastIndex) {
                            Box(
                                Modifier
                                    .fillMaxWidth(lineRightProgress)
                                    .height(3.dp)
                                    .background(activo)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    paso.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (isPassedOrCurrent) activo else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
