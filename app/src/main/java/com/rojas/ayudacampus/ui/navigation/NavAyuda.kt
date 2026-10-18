package com.rojas.ayudacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rojas.ayudacampus.AyudaApp
import com.rojas.ayudacampus.ui.cliente.SplashScreen
import com.rojas.ayudacampus.ui.soporte.CategoriasScreen
import com.rojas.ayudacampus.viewmodel.AyudaViewModelFactory
import com.rojas.ayudacampus.viewmodel.CategoriasViewModel
import androidx.navigation.compose.composable

@Composable
fun NavAyuda(app: AyudaApp) {
    val nav = rememberNavController()
    val fabrica = AyudaViewModelFactory(app.repository)

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            // TODO (Paso 6): decidir si va al Login o a la pantalla del perfil guardado
            SplashScreen(alTerminar = {
                nav.navigate(Rutas.CATEGORIAS) {
                    popUpTo(Rutas.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = fabrica)
            CategoriasScreen(vm = vm, onEditar = { /* TODO (Paso 11): abrir el formulario */ })
        }

        // TODO (Pasos 6 al 12): Login, grafo del estudiante y grafo de soporte
    }
}
