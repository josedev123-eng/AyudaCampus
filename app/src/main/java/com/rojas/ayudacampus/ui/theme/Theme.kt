package com.rojas.ayudacampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rojas.ayudacampus.model.Rol

private val ClienteClaro = lightColorScheme(
    primary = CianPrimario,
    onPrimary = Color.White,
    primaryContainer = CianContenedor,
    onPrimaryContainer = Color(0xFF164E63),
    secondary = Color(0xFF0E7490),
    secondaryContainer = CianContenedor,
    background = CianFondo,
    surface = Color.White,
    surfaceVariant = Color(0xFFE6F4F7)
)

private val SoporteClaro = lightColorScheme(
    primary = VioletaPrimario,
    onPrimary = Color.White,
    primaryContainer = VioletaContenedor,
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = Color(0xFF6D28D9),
    secondaryContainer = VioletaContenedor,
    background = VioletaFondo,
    surface = Color.White,
    surfaceVariant = Color(0xFFF0EBFA)
)

@Composable
fun AyudaTheme(
    rol: Rol = Rol.CLIENTE,
    content: @Composable () -> Unit
) {
    val esquema = if (rol == Rol.ADMIN) SoporteClaro else ClienteClaro
    MaterialTheme(
        colorScheme = esquema,
        typography = AyudaTypography,
        content = content
    )
}