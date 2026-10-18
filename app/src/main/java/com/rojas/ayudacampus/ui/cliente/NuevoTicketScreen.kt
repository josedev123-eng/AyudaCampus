package com.rojas.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.Prioridad
import com.rojas.ayudacampus.ui.components.AyudaButton
import com.rojas.ayudacampus.ui.components.AyudaTextField
import com.rojas.ayudacampus.ui.components.colorPrioridad
import com.rojas.ayudacampus.ui.components.iconoCategoria
import com.rojas.ayudacampus.viewmodel.NuevoTicketViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NuevoTicketScreen(
    vm: NuevoTicketViewModel,
    onEnviado: () -> Unit,
    onAtras: () -> Unit
) {
    val categorias by vm.categorias.collectAsState()
    val categoriaId by vm.categoriaId.collectAsState()
    val titulo by vm.titulo.collectAsState()
    val ubicacion by vm.ubicacion.collectAsState()
    val prioridad by vm.prioridad.collectAsState()
    val descripcion by vm.descripcion.collectAsState()
    val errorCategoria by vm.errorCategoria.collectAsState()
    val errorTitulo by vm.errorTitulo.collectAsState()
    val errorDescripcion by vm.errorDescripcion.collectAsState()
    val enviado by vm.enviado.collectAsState()

    // Cuando se crea el ticket se vuelve a Mis tickets
    LaunchedEffect(enviado) {
        if (enviado) onEnviado()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo ticket", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ---------- Categoría ----------
            Text("Categoría", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { c ->
                    FilterChip(
                        selected = categoriaId == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
                        label = { Text(c.nombre) },
                        leadingIcon = {
                            Icon(
                                iconoCategoria(c.icono),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            if (errorCategoria != null) {
                Text(
                    errorCategoria ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ---------- Título y ubicación ----------
            AyudaTextField(
                valor = titulo,
                onCambio = { vm.cambiarTitulo(it) },
                etiqueta = "Título",
                error = errorTitulo
            )
            AyudaTextField(
                valor = ubicacion,
                onCambio = { vm.cambiarUbicacion(it) },
                etiqueta = "Ubicación",
                icono = Icons.Filled.Place
            )

            // ---------- Prioridad ----------
            Text("Prioridad", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Prioridad.entries.forEach { p ->
                    OpcionPrioridad(
                        prioridad = p,
                        elegida = prioridad == p,
                        onClick = { vm.cambiarPrioridad(p) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---------- Descripción ----------
            AyudaTextField(
                valor = descripcion,
                onCambio = { vm.cambiarDescripcion(it) },
                etiqueta = "Descripción",
                error = errorDescripcion,
                lineas = 4
            )

            // Aviso del tiempo de respuesta según la prioridad
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Tiempo de respuesta estimado: ${tiempoEstimado(prioridad)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            AyudaButton(texto = "Enviar ticket", onClick = { vm.enviar() })
        }
    }
}

// Botón de prioridad: se pinta con el color de la prioridad cuando está elegido
@Composable
private fun OpcionPrioridad(
    prioridad: Prioridad,
    elegida: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val color = colorPrioridad(prioridad.name)
    Surface(
        onClick = onClick,
        color = if (elegida) color else MaterialTheme.colorScheme.surface,
        contentColor = if (elegida) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = if (elegida) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Text(
            prioridad.etiqueta,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

private fun tiempoEstimado(p: Prioridad): String = when (p) {
    Prioridad.ALTA -> "4 horas"
    Prioridad.MEDIA -> "24 horas"
    Prioridad.BAJA -> "72 horas"
}
