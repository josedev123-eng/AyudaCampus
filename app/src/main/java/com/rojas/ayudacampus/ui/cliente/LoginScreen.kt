package com.rojas.ayudacampus.ui.cliente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rojas.ayudacampus.data.local.entity.Usuario
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.components.AyudaButton
import com.rojas.ayudacampus.ui.components.AyudaTextField
import com.rojas.ayudacampus.ui.theme.AyudaTheme
import com.rojas.ayudacampus.viewmodel.SesionViewModel

// Un solo login para los dos roles: el color cambia según la pestaña elegida
@Composable
fun LoginScreen(
    vm: SesionViewModel,
    onIngresar: (Usuario) -> Unit,
    onRegistrarse: () -> Unit = {}
) {
    val usuario by vm.usuarioActual.collectAsState()
    val error by vm.error.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    // Cuando el login es correcto, se navega a la pantalla del rol
    LaunchedEffect(usuario) {
        usuario?.let { onIngresar(it) }
    }

    AyudaTheme(rol = rol, modoOscuro = oscuro) {
        Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(Modifier.height(48.dp))
                Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Ingresa para continuar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))

                SelectorRol(rol = rol, onCambio = {
                    rol = it
                    vm.limpiarError()
                })
                Spacer(Modifier.height(20.dp))

                AyudaTextField(
                    valor = correo,
                    onCambio = { correo = it },
                    etiqueta = "Correo",
                    icono = Icons.Outlined.Email
                )
                Spacer(Modifier.height(8.dp))
                AyudaTextField(
                    valor = clave,
                    onCambio = { clave = it },
                    etiqueta = "Contraseña",
                    esClave = true,
                    icono = Icons.Filled.Lock
                )
                Text(
                    "¿Olvidaste tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                // Mensaje de error del ViewModel
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(24.dp))
                AyudaButton(texto = "Ingresar", onClick = { vm.login(correo, clave, rol) })
                Spacer(Modifier.height(20.dp))

                if (rol == Rol.CLIENTE) {
                    Text(
                        "¿No tienes cuenta?  Regístrate",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRegistrarse() }
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Modo práctica",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// Selector Estudiante / Soporte TI
@Composable
private fun SelectorRol(rol: Rol, onCambio: (Rol) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(4.dp)) {
            OpcionRol("Estudiante", rol == Rol.CLIENTE, Modifier.weight(1f)) { onCambio(Rol.CLIENTE) }
            OpcionRol("Soporte TI", rol == Rol.ADMIN, Modifier.weight(1f)) { onCambio(Rol.ADMIN) }
        }
    }
}

@Composable
private fun OpcionRol(texto: String, elegido: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (elegido) MaterialTheme.colorScheme.primary else Color.Transparent,
        contentColor = if (elegido) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Text(
            texto,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}
