package com.rojas.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.ui.theme.CianPrimario
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.EstadoResuelto
import com.rojas.ayudacampus.viewmodel.MisTicketsViewModel
import com.rojas.ayudacampus.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    misTicketsVm: MisTicketsViewModel,
    sesionVm: SesionViewModel,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onAtras: () -> Unit
) {
    val abiertos by misTicketsVm.cantidadAbiertos.collectAsState()
    val resueltos by misTicketsVm.cantidadResueltos.collectAsState()
    val total by misTicketsVm.cantidadTotal.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Datos del usuario
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(
                            usuario.correo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                "Estudiante",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Contadores de sus tickets
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ContadorPerfil("Abiertos", abiertos, EstadoAbierto, Modifier.weight(1f))
                ContadorPerfil("Resueltos", resueltos, EstadoResuelto, Modifier.weight(1f))
                ContadorPerfil("Total", total, CianPrimario, Modifier.weight(1f))
            }

            // Cambiar de perfil (modo práctica)
            Surface(
                onClick = onCambiarPerfil,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Cambiar de perfil", fontWeight = FontWeight.Bold)
                        Text("Modo práctica: ir a vista Soporte TI", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }

            // Preferencias guardadas en DataStore
            Text("Preferencias", style = MaterialTheme.typography.titleSmall)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    FilaSwitch("Modo oscuro", "Cambia el tema de la app", oscuro) {
                        sesionVm.guardarModoOscuro(it)
                    }
                    HorizontalDivider()
                    FilaSwitch("Notificaciones", "Avisar cambios de estado", notificaciones) {
                        sesionVm.guardarNotificaciones(it)
                    }
                }
            }

            // Nota
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "Se guardan en el dispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "con DataStore",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Cerrar sesión
            OutlinedButton(
                onClick = onCerrarSesion,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ContadorPerfil(titulo: String, valor: Int, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("$valor", style = MaterialTheme.typography.headlineMedium, color = color)
            Text(titulo, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

@Composable
private fun FilaSwitch(titulo: String, subtitulo: String, valor: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = valor, onCheckedChange = onCambio)
    }
}
