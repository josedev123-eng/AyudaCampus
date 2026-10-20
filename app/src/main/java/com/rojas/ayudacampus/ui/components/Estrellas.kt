package com.rojas.ayudacampus.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.ui.theme.ColorEstrella

// Si onCambio es null las estrellas solo se muestran (not touchable)
@Composable
fun Estrellas(
    valor: Int,
    onCambio: ((Int) -> Unit)? = null,
    tamano: Dp = 36.dp
) {
    Row {
        for (i in 1..5) {
            val isSelected = i <= valor
            var tapped by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (tapped) 1.3f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                finishedListener = { tapped = false },
                label = "starScale_$i"
            )

            val base = Modifier
                .size(tamano)
                .scale(scale)

            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "$i estrellas",
                tint = ColorEstrella,
                modifier = if (onCambio != null) {
                    base.clickable {
                        tapped = true
                        onCambio(i)
                    }
                } else {
                    base
                }
            )
        }
    }
}
