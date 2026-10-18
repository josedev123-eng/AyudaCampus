package com.rojas.ayudacampus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.ui.theme.ColorEstrella

// Si onCambio es null las estrellas solo se muestran (no se pueden tocar)
@Composable
fun Estrellas(
    valor: Int,
    onCambio: ((Int) -> Unit)? = null,
    tamano: Dp = 36.dp
) {
    Row {
        for (i in 1..5) {
            val base = Modifier.size(tamano)
            Icon(
                imageVector = if (i <= valor) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "$i estrellas",
                tint = ColorEstrella,
                modifier = if (onCambio != null) base.clickable { onCambio(i) } else base
            )
        }
    }
}
