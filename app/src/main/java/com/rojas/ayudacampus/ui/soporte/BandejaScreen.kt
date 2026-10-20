package com.rojas.ayudacampus.ui.soporte

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.ui.components.TicketCard
import com.rojas.ayudacampus.viewmodel.BandejaViewModel

// Clave del filtro -> texto del chip
private val FILTROS_BANDEJA = listOf(
    "TODOS" to "Todos",
    "ABIERTO" to "Abiertos",
    "EN_PROCESO" to "En proceso",
    "RESUELTO" to "Resueltos"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val filtro by vm.filtro.collectAsState()

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
            // Chips de estado
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FILTROS_BANDEJA.forEach { (clave, texto) ->
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
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Inbox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Text("No hay tickets aquí", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
