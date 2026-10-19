package com.rojas.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rojas.ayudacampus.AyudaApp
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.cliente.DetalleTicketScreen
import com.rojas.ayudacampus.ui.cliente.LoginScreen
import com.rojas.ayudacampus.ui.cliente.MisTicketsScreen
import com.rojas.ayudacampus.ui.cliente.NuevoTicketScreen
import com.rojas.ayudacampus.ui.cliente.SplashScreen
import com.rojas.ayudacampus.ui.soporte.CategoriasScreen
import com.rojas.ayudacampus.ui.theme.AyudaTheme
import com.rojas.ayudacampus.viewmodel.AyudaViewModelFactory
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel
import com.rojas.ayudacampus.viewmodel.MisTicketsViewModel
import com.rojas.ayudacampus.viewmodel.NuevoTicketViewModel
import com.rojas.ayudacampus.viewmodel.SesionViewModel
import com.rojas.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun NavAyuda(app: AyudaApp) {
    val nav = rememberNavController()
    val fabrica = AyudaViewModelFactory(app.repository, app.preferencias)

    // La sesión se crea una sola vez y la comparten todas las pantallas
    val sesionVm: SesionViewModel = viewModel(factory = fabrica)
    val usuario by sesionVm.usuarioActual.collectAsState()

    // Ruta actual, para saber qué ítem de la barra marcar
    val entradaActual by nav.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route ?: ""

    // Cian para estudiante y violeta para Soporte
    val rolActual = if (usuario?.rol == Rol.ADMIN.name) Rol.ADMIN else Rol.CLIENTE

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

    // Cambia de pestaña sin apilar pantallas repetidas
    fun irATab(ruta: String) {
        nav.navigate(ruta) {
            popUpTo(Rutas.MIS_TICKETS)
            launchSingleTop = true
        }
    }

    AyudaTheme(rol = rolActual) {
        Scaffold(
            bottomBar = {
                // Barra del estudiante: Tickets, Nuevo y Perfil
                if (rutaActual in Rutas.TABS_CLIENTE) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.MIS_TICKETS,
                            onClick = { irATab(Rutas.MIS_TICKETS) },
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                            label = { Text("Tickets") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.NUEVO_TICKET,
                            onClick = { irATab(Rutas.NUEVO_TICKET) },
                            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                            label = { Text("Nuevo") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.PERFIL,
                            onClick = { irATab(Rutas.PERFIL) },
                            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            label = { Text("Perfil") }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = Rutas.SPLASH,
                modifier = Modifier.padding(padding)
            ) {

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
                    MisTicketsScreen(
                        vm = vm,
                        usuario = u,
                        onAbrir = { id -> nav.navigate(Rutas.detalle(id)) },
                        onNuevo = { irATab(Rutas.NUEVO_TICKET) }
                    )
                }

                composable(Rutas.NUEVO_TICKET) {
                    val u = usuario ?: return@composable
                    val vm: NuevoTicketViewModel = viewModel(
                        factory = AyudaViewModelFactory(app.repository, app.preferencias, u)
                    )
                    NuevoTicketScreen(
                        vm = vm,
                        onEnviado = { nav.popBackStack() },
                        onAtras = { nav.popBackStack() }
                    )
                }

                composable(
                    Rutas.DETALLE,
                    arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                ) { entrada ->
                    val u = usuario ?: return@composable
                    val id = entrada.arguments?.getInt("ticketId") ?: 0
                    val vm: TicketDetalleViewModel = viewModel(
                        factory = AyudaViewModelFactory(app.repository, app.preferencias, u, id)
                    )
                    DetalleTicketScreen(vm = vm, usuario = u, onAtras = { nav.popBackStack() })
                }

                // TODO (Paso 12): reemplazar por PerfilScreen
                composable(Rutas.PERFIL) {
                    PantallaTemporal("Perfil", onCerrarSesion = { cerrarSesion() })
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

                // TODO (Pasos 10 al 12): pantallas de Soporte y su barra
            }
        }
    }
}

@Composable
private fun PantallaTemporal(titulo: String, onCerrarSesion: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}