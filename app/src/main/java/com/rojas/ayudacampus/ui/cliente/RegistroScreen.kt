package com.rojas.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.components.AyudaButton
import com.rojas.ayudacampus.ui.components.AyudaTextField
import com.rojas.ayudacampus.ui.theme.AyudaTheme
import com.rojas.ayudacampus.ui.theme.EstadoAbierto
import com.rojas.ayudacampus.ui.theme.EstadoResuelto
import com.rojas.ayudacampus.ui.theme.PrioridadAlta
import com.rojas.ayudacampus.viewmodel.FortalezaClave
import com.rojas.ayudacampus.viewmodel.RegistroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    vm: RegistroViewModel,
    onRegistrado: () -> Unit,
    onAtras: () -> Unit
) {
    val nombre by vm.nombre.collectAsState()
    val correo by vm.correo.collectAsState()
    val clave by vm.clave.collectAsState()
    val confirmarClave by vm.confirmarClave.collectAsState()
    val errores by vm.errores.collectAsState()
    val registrado by vm.registrado.collectAsState()
    val fortaleza by vm.fortaleza.collectAsState()
    val requisitos by vm.requisitosClave.collectAsState()

    var claveVisible by remember { mutableStateOf(false) }
    var confirmarClaveVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registrado) {
        if (registrado) {
            onRegistrado()
        }
    }

    val (progreso, colorFortaleza, textoFortaleza) = when (fortaleza) {
        FortalezaClave.DEBIL -> Triple(0.33f, PrioridadAlta, "Débil")
        FortalezaClave.MEDIA -> Triple(0.66f, EstadoAbierto, "Media")
        FortalezaClave.FUERTE -> Triple(1.0f, EstadoResuelto, "Fuerte")
    }

    AyudaTheme(rol = Rol.CLIENTE) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Crear cuenta", fontWeight = FontWeight.Bold) },
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
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text("Regístrate como estudiante", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Crea tu cuenta para enviar y dar seguimiento a tus tickets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))

                // 1. Nombre completo
                AyudaTextField(
                    valor = nombre,
                    onCambio = { vm.cambiarNombre(it) },
                    etiqueta = "Nombre completo",
                    error = errores.nombre,
                    icono = Icons.Filled.Person
                )
                Spacer(Modifier.height(12.dp))

                // 2. Correo institucional
                AyudaTextField(
                    valor = correo,
                    onCambio = { vm.cambiarCorreo(it) },
                    etiqueta = "Correo electrónico",
                    error = errores.correo,
                    icono = Icons.Outlined.Email
                )
                Spacer(Modifier.height(12.dp))

                // 3. Contraseña con ícono de ojo para mostrar/ocultar
                OutlinedTextField(
                    value = clave,
                    onValueChange = { vm.cambiarClave(it) },
                    label = { Text("Contraseña") },
                    isError = errores.clave != null,
                    supportingText = if (errores.clave != null) {
                        { Text(errores.clave!!) }
                    } else null,
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { claveVisible = !claveVisible }) {
                            Icon(
                                imageVector = if (claveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (claveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Indicador de fortaleza de la contraseña
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LinearProgressIndicator(
                        progress = { progreso },
                        color = colorFortaleza,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = textoFortaleza,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colorFortaleza
                    )
                }

                // Lista de requisitos con check verde o X gris
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    requisitos.forEach { req ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (req.cumplido) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null,
                                tint = if (req.cumplido) EstadoResuelto else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = req.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (req.cumplido) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // 4. Confirmar contraseña con ícono de ojo
                OutlinedTextField(
                    value = confirmarClave,
                    onValueChange = { vm.cambiarConfirmarClave(it) },
                    label = { Text("Confirmar contraseña") },
                    isError = errores.confirmarClave != null,
                    supportingText = if (errores.confirmarClave != null) {
                        { Text(errores.confirmarClave!!) }
                    } else null,
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { confirmarClaveVisible = !confirmarClaveVisible }) {
                            Icon(
                                imageVector = if (confirmarClaveVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmarClaveVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (confirmarClaveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(28.dp))

                AyudaButton(
                    texto = "Crear cuenta",
                    onClick = { vm.registrar() }
                )
            }
        }
    }
}
