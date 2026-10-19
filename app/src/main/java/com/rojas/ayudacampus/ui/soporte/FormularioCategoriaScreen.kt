package com.rojas.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.entity.Categoria
import com.rojas.ayudacampus.ui.components.AyudaButton
import com.rojas.ayudacampus.ui.components.AyudaTextField
import com.rojas.ayudacampus.ui.components.ICONOS_CATEGORIA
import com.rojas.ayudacampus.ui.components.iconoCategoria
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel

// Sirve para crear (id = 0) y para editar (id de la categoría)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormularioCategoriaScreen(
    vm: CategoriasViewModel,
    id: Int,
    onVolver: () -> Unit
) {
    val categorias by vm.categorias.collectAsState()
    val conteos by vm.conteos.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    var nombre by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("pc") }
    var activa by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var cargado by remember { mutableStateOf(false) }

    // Si es edición, llena los campos cuando llega la categoría
    val original = categorias.find { it.id == id }
    LaunchedEffect(original) {
        if (original != null && !cargado) {
            nombre = original.nombre
            icono = original.icono
            activa = original.activa
            cargado = true
        }
    }

    // Avisos del ViewModel (por ejemplo, que no se puede eliminar)
    LaunchedEffect(mensaje) {
        val m = mensaje
        if (m != null) {
            snackbar.showSnackbar(m)
            vm.limpiarMensaje()
        }
    }

    // Tickets de esta categoría para la vista previa
    val cantidad = if (original == null) 0 else conteos.find { it.nombre == original.nombre }?.cantidad ?: 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (id == 0) "Nueva categoría" else "Editar categoría",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
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
            // Nombre (obligatorio)
            AyudaTextField(
                valor = nombre,
                onCambio = {
                    nombre = it
                    error = null
                },
                etiqueta = "Nombre de la categoría",
                error = error
            )

            // Selector de icono: 6 opciones en 2 filas
            Text("Ícono", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 3,
                modifier = Modifier.fillMaxWidth()
            ) {
                ICONOS_CATEGORIA.forEach { opcion ->
                    val elegido = opcion == icono
                    Surface(
                        onClick = { icono = opcion },
                        color = if (elegido) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            if (elegido) 2.dp else 1.dp,
                            if (elegido) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                iconoCategoria(opcion),
                                contentDescription = opcion,
                                tint = if (elegido) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Switch "Activa"
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Categoría activa", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Los estudiantes podrán elegirla",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }

            // Vista previa
            Text("Vista previa", style = MaterialTheme.typography.titleSmall)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            iconoCategoria(icono),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            nombre.ifBlank { "Nombre de la categoría" },
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "$cantidad tickets" + if (activa) "" else " · Inactiva",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            AyudaButton(
                texto = "Guardar categoría",
                onClick = {
                    val ok = vm.guardar(Categoria(id = id, nombre = nombre, icono = icono, activa = activa))
                    if (ok) onVolver() else error = "El nombre es obligatorio"
                }
            )
            OutlinedButton(
                onClick = onVolver,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }

            // Solo al editar: eliminar (falla si la categoría ya tiene tickets)
            if (original != null) {
                TextButton(
                    onClick = { vm.eliminar(original) { eliminada -> if (eliminada) onVolver() } },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar categoría", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
