package com.rojas.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rojas.ayudacampus.AyudaApp
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.cliente.DetalleTicketScreen
import com.rojas.ayudacampus.ui.cliente.LoginScreen
import com.rojas.ayudacampus.ui.cliente.MisTicketsScreen
import com.rojas.ayudacampus.ui.cliente.NuevoTicketScreen
import com.rojas.ayudacampus.ui.cliente.PerfilScreen
import com.rojas.ayudacampus.ui.cliente.SplashScreen
import com.rojas.ayudacampus.ui.soporte.AjustesScreen
import com.rojas.ayudacampus.ui.soporte.AtenderTicketScreen
import com.rojas.ayudacampus.ui.soporte.BandejaScreen
import com.rojas.ayudacampus.ui.soporte.CategoriasScreen
import com.rojas.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.rojas.ayudacampus.ui.soporte.PanelScreen
import com.rojas.ayudacampus.ui.theme.AyudaTheme
import com.rojas.ayudacampus.viewmodel.AtenderViewModel
import com.rojas.ayudacampus.viewmodel.AyudaViewModelFactory
import com.rojas.ayudacampus.viewmodel.BandejaViewModel
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel
import com.rojas.ayudacampus.viewmodel.MisTicketsViewModel
import com.rojas.ayudacampus.viewmodel.NuevoTicketViewModel
import com.rojas.ayudacampus.viewmodel.PanelViewModel
import com.rojas.ayudacampus.viewmodel.SesionViewModel
import com.rojas.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun NavAyuda(app: AyudaApp) {
    val nav = rememberNavController()
    val fabrica = AyudaViewModelFactory(app.repository, app.preferencias)

    // La sesión se crea una sola vez y la comparten todas las pantallas
    val sesionVm: SesionViewModel = viewModel(factory = fabrica)
    val usuario by sesionVm.usuarioActual.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val recibirTickets by sesionVm.recibirTickets.collectAsState()

    // Ruta actual, para saber qué ítem de la barra marcar
    val entradaActual by nav.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route ?: ""

    // Cian para estudiante y violeta para Soporte
    val rolActual = if (usuario?.rol == Rol.ADMIN.name) Rol.ADMIN else Rol.CLIENTE

    // Va al grafo del rol y limpia toda la pila (popUpTo)
    fun irAPrincipal(rol: String) {
        val grafo = if (rol == Rol.ADMIN.name) Rutas.GRAFO_SOPORTE else Rutas.GRAFO_CLIENTE
        nav.navigate(grafo) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    fun irALogin() {
        nav.navigate(Rutas.LOGIN) {
            popUpTo(nav.graph.id) { inclusive = true }
        }
    }

    // Borra el perfil y vuelve al Login
    fun cerrarSesion() {
        sesionVm.cerrarSesion()
        irALogin()
    }

    // Guarda el otro perfil y navega a su grafo
    fun cambiarPerfil() {
        sesionVm.cambiarPerfil { otro -> irAPrincipal(otro.rol) }
    }

    // Cambia de pestaña sin apilar pantallas repetidas
    // (inicio = primera pantalla del perfil: Mis tickets o Panel)
    fun irATab(ruta: String, inicio: String = Rutas.MIS_TICKETS) {
        nav.navigate(ruta) {
            popUpTo(inicio)
            launchSingleTop = true
        }
    }

    // El modo oscuro cambia el tema de TODA la app al instante
    AyudaTheme(rol = rolActual, modoOscuro = oscuro) {
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
                // Barra de Soporte: Panel, Tickets, Categorías y Ajustes
                if (rutaActual in Rutas.TABS_SOPORTE) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.PANEL,
                            onClick = { irATab(Rutas.PANEL, Rutas.PANEL) },
                            icon = { Icon(Icons.Filled.BarChart, contentDescription = null) },
                            label = { Text("Panel") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.BANDEJA,
                            onClick = { irATab(Rutas.BANDEJA, Rutas.PANEL) },
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                            label = { Text("Tickets") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.CATEGORIAS,
                            onClick = { irATab(Rutas.CATEGORIAS, Rutas.PANEL) },
                            icon = { Icon(Icons.Filled.LocalOffer, contentDescription = null) },
                            label = { Text("Categorías") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.AJUSTES,
                            onClick = { irATab(Rutas.AJUSTES, Rutas.PANEL) },
                            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                            label = { Text("Ajustes") }
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

                // ================= Grafo del estudiante =================
                navigation(route = Rutas.GRAFO_CLIENTE, startDestination = Rutas.MIS_TICKETS) {

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
                            recibirTickets = recibirTickets,
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

                    composable(Rutas.PERFIL) {
                        val u = usuario ?: return@composable
                        val vm: MisTicketsViewModel = viewModel(
                            factory = AyudaViewModelFactory(app.repository, app.preferencias, u)
                        )
                        PerfilScreen(
                            usuario = u,
                            misTicketsVm = vm,
                            sesionVm = sesionVm,
                            onCambiarPerfil = { cambiarPerfil() },
                            onCerrarSesion = { cerrarSesion() },
                            onAtras = { nav.popBackStack() }
                        )
                    }
                }

                // ================= Grafo de Soporte TI =================
                navigation(route = Rutas.GRAFO_SOPORTE, startDestination = Rutas.PANEL) {

                    composable(Rutas.PANEL) {
                        val vm: PanelViewModel = viewModel(factory = fabrica)
                        PanelScreen(vm = vm, onVerBandeja = { irATab(Rutas.BANDEJA, Rutas.PANEL) })
                    }

                    composable(Rutas.BANDEJA) {
                        val vm: BandejaViewModel = viewModel(factory = fabrica)
                        BandejaScreen(
                            vm = vm,
                            onAtender = { id -> nav.navigate(Rutas.atender(id)) },
                            onAtras = { nav.popBackStack() }
                        )
                    }

                    composable(
                        Rutas.ATENDER,
                        arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
                    ) { entrada ->
                        val u = usuario ?: return@composable
                        val id = entrada.arguments?.getInt("ticketId") ?: 0
                        val vm: AtenderViewModel = viewModel(
                            factory = AyudaViewModelFactory(app.repository, app.preferencias, u, id)
                        )
                        AtenderTicketScreen(vm = vm, onAtras = { nav.popBackStack() })
                    }

                    composable(Rutas.CATEGORIAS) {
                        val vm: CategoriasViewModel = viewModel(factory = fabrica)
                        CategoriasScreen(vm = vm, onEditar = { id -> nav.navigate(Rutas.formCategoria(id)) })
                    }

                    composable(
                        Rutas.FORM_CATEGORIA,
                        arguments = listOf(navArgument("id") {
                            type = NavType.IntType
                            defaultValue = 0
                        })
                    ) { entrada ->
                        val id = entrada.arguments?.getInt("id") ?: 0
                        val padre = remember(entrada) { nav.getBackStackEntry(Rutas.CATEGORIAS) }
                        val vm: CategoriasViewModel = viewModel(viewModelStoreOwner = padre, factory = fabrica)
                        FormularioCategoriaScreen(vm = vm, id = id, onVolver = { nav.popBackStack() })
                    }

                    composable(Rutas.AJUSTES) {
                        val u = usuario ?: return@composable
                        AjustesScreen(
                            usuario = u,
                            sesionVm = sesionVm,
                            onCambiarPerfil = { cambiarPerfil() },
                            onCerrarSesion = { cerrarSesion() },
                            onAtras = { nav.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}