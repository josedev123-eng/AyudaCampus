package com.rojas.ayudacampus.ui.soporte

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.Prioridad
import com.rojas.ayudacampus.ui.components.TicketCard
import com.rojas.ayudacampus.ui.components.iconoCategoria
import com.rojas.ayudacampus.viewmodel.BandejaViewModel

// Clave del filtro -> texto del chip
private val FILTROS_BANDEJA = listOf(
    "TODOS" to "Todos",
    "ABIERTO" to "Abiertos",
    "EN_PROCESO" to "En proceso",
    "RESUELTO" to "Resueltos",
    "CERRADO" to "Cerrados"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val filtros by vm.filtros.collectAsState()
    val categorias by vm.categorias.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tickets", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = filtros.busqueda,
                onValueChange = { vm.cambiarBusqueda(it) },
                placeholder = { Text("Buscar por título, número o cliente...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // Chips de estado
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FILTROS_BANDEJA.forEach { (clave, texto) ->
                    FilterChip(
                        selected = filtros.estado == clave,
                        onClick = { vm.cambiarEstado(clave) },
                        label = { Text(texto) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(Modifier.height(6.dp))

            // Chips de prioridad
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = filtros.prioridad == null,
                    onClick = { vm.cambiarPrioridad(null) },
                    label = { Text("Todas las prioridades") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Prioridad.entries.forEach { prio ->
                    FilterChip(
                        selected = filtros.prioridad == prio,
                        onClick = {
                            vm.cambiarPrioridad(if (filtros.prioridad == prio) null else prio)
                        },
                        label = { Text(prio.etiqueta) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(Modifier.height(6.dp))

            // Chips de categoría
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = filtros.categoriaId == null,
                    onClick = { vm.cambiarCategoria(null) },
                    label = { Text("Todas las categorías") },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                categorias.forEach { cat ->
                    FilterChip(
                        selected = filtros.categoriaId == cat.id,
                        onClick = {
                            vm.cambiarCategoria(if (filtros.categoriaId == cat.id) null else cat.id)
                        },
                        label = { Text(cat.nombre) },
                        leadingIcon = {
                            Icon(
                                iconoCategoria(cat.icono),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Botón Limpiar filtros (solo si hay filtros activos)
            if (filtros.hayFiltrosActivos()) {
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { vm.limpiarFiltros() }) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Limpiar filtros")
                    }
                }
            } else {
                Spacer(Modifier.height(8.dp))
            }

            if (tickets.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Inbox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (filtros.hayFiltrosActivos()) "No hay tickets con esos filtros"
                            else "No hay tickets aquí",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Ya vienen ordenados por prioridad (ALTA, MEDIA, BAJA) desde el DAO
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
                ) {
                    items(tickets, key = { it.item.ticket.id }) { itemSla ->
                        TicketCard(
                            item = itemSla.item,
                            sla = itemSla.sla,
                            onClick = { onAtender(itemSla.item.ticket.id) },
                            mostrarCliente = true
                        )
                    }
                }
            }
        }
    }
}
