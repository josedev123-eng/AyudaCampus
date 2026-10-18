package com.rojas.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rojas.ayudacampus.AyudaApp
import com.rojas.ayudacampus.model.Rol
import com.rojas.ayudacampus.ui.cliente.LoginScreen
import com.rojas.ayudacampus.ui.cliente.SplashScreen
import com.rojas.ayudacampus.ui.soporte.CategoriasScreen
import com.rojas.ayudacampus.viewmodel.AyudaViewModelFactory
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel
import com.rojas.ayudacampus.viewmodel.SesionViewModel

@Composable
fun NavAyuda(app: AyudaApp) {
    val nav = rememberNavController()
    val fabrica = AyudaViewModelFactory(app.repository, app.preferencias)

    // La sesión se crea una sola vez y la comparten todas las pantallas
    val sesionVm: SesionViewModel = viewModel(factory = fabrica)

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

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            // Si ya hay perfil guardado salta el login
            SplashScreen(alTerminar = {
                sesionVm.restaurarSesion { usuario ->
                    if (usuario == null) irALogin() else irAPrincipal(usuario.rol)
                }
            })
        }

        composable(Rutas.LOGIN) {
            LoginScreen(vm = sesionVm, onIngresar = { usuario -> irAPrincipal(usuario.rol) })
        }

        // TODO (Paso 7): reemplazar por MisTicketsScreen
        composable(Rutas.MIS_TICKETS) {
            PantallaTemporal("Mis tickets", onCerrarSesion = {
                sesionVm.cerrarSesion()
                irALogin()
            })
        }

        // TODO (Paso 10): reemplazar por PanelScreen
        composable(Rutas.PANEL) {
            PantallaTemporal("Panel de soporte", onCerrarSesion = {
                sesionVm.cerrarSesion()
                irALogin()
            })
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = fabrica)
            CategoriasScreen(vm = vm, onEditar = { /* TODO (Paso 11): abrir el formulario */ })
        }

        // TODO (Pasos 7 al 12): resto de pantallas y grafo de cada perfil
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