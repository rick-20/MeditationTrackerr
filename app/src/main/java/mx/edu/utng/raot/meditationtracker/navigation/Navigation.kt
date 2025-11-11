package mx.edu.utng.raot.meditationtracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.raot.meditationtracker.ui.screens.AddSessionScreen
import mx.edu.utng.raot.meditationtracker.ui.screens.HomeScreen
import mx.edu.utng.raot.meditationtracker.ui.viewmodel.MeditationViewModel

/**
 * Rutas de navegación
 * Sealed class: Tipo seguro para rutas
 *
 * Pedagogía: Como direcciones postales
 * Cada pantalla tiene una dirección única
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddSession : Screen("add_session")

    // Para futuras pantallas:
    // object Statistics : Screen("statistics")
    // object Settings : Screen("settings")
}

/**
 * Configuración del grafo de navegación
 *
 * NavHost: Contenedor que muestra la pantalla actual
 *
 * Analogía: Como un marco de fotos que cambia la foto mostrada
 */
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: MeditationViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // Pantalla inicial
    ) {
        /**
         * Pantalla principal (Home)
         */
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddSessionClick = {
                    // Navegar a pantalla de agregar sesión
                    navController.navigate(Screen.AddSession.route)
                }
            )
        }

        /**
         * Pantalla de agregar sesión
         */
        composable(route = Screen.AddSession.route) {
            AddSessionScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    // Regresar a pantalla anterior
                    navController.popBackStack()
                }
            )
        }
    }
}