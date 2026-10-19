package com.rojas.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.ui.components.graficos.GraficoBarras
import com.rojas.ayudacampus.ui.components.graficos.GraficoDona
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.EstadoEnProceso
import com.rojas.ayudacampus.ui.theme.EstadoResuelto
import com.rojas.ayudacampus.ui.theme.PrioridadAlta
import com.rojas.ayudacampus.viewmodel.PanelViewModel

@Composable
fun PanelScreen(
    vm: PanelViewModel,
    onVerBandeja: () -> Unit
) {
    val abiertos by vm.abiertos.collectAsState()
    val enProceso by vm.enProceso.collectAsState()
    val resueltos by vm.resueltos.collectAsState()
    val tiempoPromedio by vm.tiempoPromedio.collectAsState()
    val porEstado by vm.porEstado.collectAsState()
    val porCategoria by vm.porCategoria.collectAsState()
    val altas by vm.altasSinAtender.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Panel de soporte", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Mesa de ayuda TI · Hoy",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
                }
            }

            // 3 contadores de color
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador("Abiertos", abiertos, EstadoAbierto, Modifier.weight(1f))
                Contador("En proceso", enProceso, EstadoEnProceso, Modifier.weight(1f))
                Contador("Resueltos", resueltos, EstadoResuelto, Modifier.weight(1f))
            }

            // Tarjeta de Tiempo promedio de resolución
            TarjetaBlanca {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Reloj",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Tiempo promedio de resolución",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = tiempoPromedio,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Gráfico de dona
            Text("Tickets por estado", style = MaterialTheme.typography.titleSmall)
            TarjetaBlanca {
                GraficoDona(datos = porEstado, modifier = Modifier.fillMaxWidth())
            }

            // Gráfico de barras
            Text("Tickets por categoría", style = MaterialTheme.typography.titleSmall)
            TarjetaBlanca {
                GraficoBarras(datos = porCategoria)
            }

            // Alerta roja: ALTA y ABIERTO con más de 1 hora
            if (altas > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PrioridadAlta.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = PrioridadAlta)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (altas == 1) "1 ticket de prioridad Alta"
                                else "$altas tickets de prioridad Alta",
                                fontWeight = FontWeight.Bold,
                                color = PrioridadAlta
                            )
                            Text(
                                "sin atender hace más de 1 hora",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrioridadAlta
                            )
                        }
                        Button(
                            onClick = onVerBandeja,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrioridadAlta,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Ver", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, color: Color, modifier: Modifier) {
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
            Text(
                "$valor",
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
            Text(
                titulo,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun TarjetaBlanca(contenido: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            contenido()
        }
    }
}
