package com.rojas.ayudacampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

private val ClienteOscuro = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF083344),
    primaryContainer = Color(0xFF155E75),
    onPrimaryContainer = CianContenedor,
    secondary = Color(0xFF67E8F9),
    secondaryContainer = Color(0xFF155E75),
    background = Color(0xFF0B1416),
    surface = Color(0xFF132023),
    surfaceVariant = Color(0xFF1E2F33)
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

private val SoporteOscuro = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF2E1065),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = VioletaContenedor,
    secondary = Color(0xFFC4B5FD),
    secondaryContainer = Color(0xFF4C1D95),
    background = Color(0xFF110E18),
    surface = Color(0xFF1A1624),
    surfaceVariant = Color(0xFF282236)
)

// Entrega un ColorScheme según el perfil y el modo oscuro guardado en DataStore
@Composable
fun AyudaTheme(
    rol: Rol = Rol.CLIENTE,
    modoOscuro: Boolean = false,
    content: @Composable () -> Unit
) {
    val esquema = if (rol == Rol.ADMIN) {
        if (modoOscuro) SoporteOscuro else SoporteClaro
    } else {
        if (modoOscuro) ClienteOscuro else ClienteClaro
    }
    MaterialTheme(
        colorScheme = esquema,
        typography = AyudaTypography,
        content = content
    )
}