package com.rojas.ayudacampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.ui.components.EstadoVacio
import com.rojas.ayudacampus.ui.components.TicketCard
import com.rojas.ayudacampus.viewmodel.MisTicketsViewModel

// Clave del filtro -> texto del chip
private val FILTROS = listOf(
    "TODOS" to "Todos",
    "ABIERTO" to "Abiertos",
    "EN_PROCESO" to "En proceso",
    "RESUELTO" to "Resueltos"
)

@Composable
fun MisTicketsScreen(
    vm: MisTicketsViewModel,
    usuario: Usuario,
    notificacionesActivadas: Boolean = true,
    onAbrir: (Int) -> Unit,
    onNuevo: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val pendientesCalificar by vm.pendientesCalificar.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevo,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo ticket")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Cabecera con degradado y esquinas inferiores redondeadas de 28.dp
            val primary = MaterialTheme.colorScheme.primary
            val primaryContainer = MaterialTheme.colorScheme.primaryContainer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(Brush.verticalGradient(listOf(primary, primaryContainer)))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Hola, ${usuario.nombre.substringBefore(" ")}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "¿En qué te ayudamos hoy?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }

                    if (notificacionesActivadas) {
                        BadgedBox(
                            badge = {
                                if (pendientesCalificar > 0) {
                                    Badge { Text("$pendientesCalificar") }
                                }
                            }
                        ) {
                            IconButton(onClick = { vm.cambiarFiltro("RESUELTO") }) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // Buscador
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { vm.cambiarBusqueda(it) },
                    placeholder = { Text("Buscar por título o número...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                // Chips de filtro
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    FILTROS.forEach { (clave, texto) ->
                        FilterChip(
                            selected = filtro == clave,
                            onClick = { vm.cambiarFiltro(clave) },
                            label = { Text(texto) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                if (tickets.isEmpty()) {
                    val hayFiltroOBusqueda = busqueda.isNotBlank() || filtro != "TODOS"
                    if (hayFiltroOBusqueda) {
                        EstadoVacio(
                            icono = Icons.Default.SearchOff,
                            titulo = "Sin resultados",
                            mensaje = "Prueba con otra palabra o cambia el filtro",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        EstadoVacio(
                            icono = Icons.Filled.Inbox,
                            titulo = "Todo en orden",
                            mensaje = "Aún no tienes tickets. Si algo falla en el campus, repórtalo con el botón +",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
                    ) {
                        items(tickets, key = { it.item.ticket.id }) { itemSla ->
                            TicketCard(
                                item = itemSla.item,
                                sla = itemSla.sla,
                                onClick = { onAbrir(itemSla.item.ticket.id) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}
