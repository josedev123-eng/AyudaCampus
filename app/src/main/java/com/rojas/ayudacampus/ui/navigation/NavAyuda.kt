package com.rojas.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rojas.ayudacampus.AyudaApp
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.cliente.LoginScreen
import com.rojas.ayudacampus.ui.cliente.MisTicketsScreen
import com.rojas.ayudacampus.ui.cliente.SplashScreen
import com.rojas.ayudacampus.ui.soporte.CategoriasScreen
import com.rojas.ayudacampus.ui.theme.AyudaTheme
import com.rojas.ayudacampus.viewmodel.AyudaViewModelFactory
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel
import com.rojas.ayudacampus.viewmodel.MisTicketsViewModel
import com.rojas.ayudacampus.viewmodel.SesionViewModel
import com.rojas.ayudacampus.ui.cliente.NuevoTicketScreen
import com.rojas.ayudacampus.viewmodel.NuevoTicketViewModel

@Composable
fun NavAyuda(app: AyudaApp) {
    val nav = rememberNavController()
    val fabrica = AyudaViewModelFactory(app.repository, app.preferencias)

    // La sesión se crea una sola vez y la comparten todas las pantallas
    val sesionVm: SesionViewModel = viewModel(factory = fabrica)
    val usuario by sesionVm.usuarioActual.collectAsState()

    // Va a la pantalla principal del rol y borra Splash/Login de la pila
    fun irAPrincipal(rol: String) {
        val destino = if (rol == Rol.ADMIN.name) Rutas.PANEL else Rutas.MIS_TICKETS
        nav.navigate(destino) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    fun irALogin() {
        nav.navigate(Rutas.LOGIN) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    fun cerrarSesion() {
        sesionVm.cerrarSesion()
        irALogin()
    }

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            // Si ya hay perfil guardado salta el login
            SplashScreen(alTerminar = {
                sesionVm.restaurarSesion { u ->
                    if (u == null) irALogin() else irAPrincipal(u.rol)
                }
            })
        }

        composable(Rutas.LOGIN) {
            LoginScreen(vm = sesionVm, onIngresar = { u -> irAPrincipal(u.rol) })
        }

        // ---------- Estudiante ----------

        composable(Rutas.MIS_TICKETS) {
            val u = usuario ?: return@composable
            val vm: MisTicketsViewModel = viewModel(
                factory = AyudaViewModelFactory(app.repository, app.preferencias, u)
            )
            AyudaTheme(rol = Rol.CLIENTE) {
                MisTicketsScreen(
                    vm = vm,
                    usuario = u,
                    onAbrir = { id -> nav.navigate(Rutas.detalle(id)) },
                    onNuevo = { nav.navigate(Rutas.NUEVO_TICKET) }
                )
            }
        }

        composable(Rutas.NUEVO_TICKET) {
            val u = usuario ?: return@composable
            val vm: NuevoTicketViewModel = viewModel(
                factory = AyudaViewModelFactory(app.repository, app.preferencias, u)
            )
            AyudaTheme(rol = Rol.CLIENTE) {
                NuevoTicketScreen(
                    vm = vm,
                    onEnviado = { nav.popBackStack() },
                    onAtras = { nav.popBackStack() }
                )
            }
        }

        // TODO (Paso 9): reemplazar por DetalleTicketScreen
        composable(Rutas.DETALLE) {
            PantallaTemporal(
                "Detalle del ticket",
                onVolver = { nav.popBackStack() },
                onCerrarSesion = { cerrarSesion() }
            )
        }

        // ---------- Soporte TI ----------

        // TODO (Paso 10): reemplazar por PanelScreen
        composable(Rutas.PANEL) {
            PantallaTemporal("Panel de soporte", onCerrarSesion = { cerrarSesion() })
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = fabrica)
            CategoriasScreen(vm = vm, onEditar = { /* TODO (Paso 11): abrir el formulario */ })
        }

        // TODO (Pasos 8 al 12): resto de pantallas y grafo de cada perfil
    }
}

// Solo para probar mientras no existen las pantallas reales
@Composable
private fun PantallaTemporal(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    onCerrarSesion: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        if (onVolver != null) {
            TextButton(onClick = onVolver) { Text("Volver") }
        }
        TextButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}