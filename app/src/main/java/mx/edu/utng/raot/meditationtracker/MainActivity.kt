package mx.edu.utng.raot.meditationtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utng.raot.meditationtracker.data.database.MeditationDatabase
import mx.edu.utng.raot.meditationtracker.data.repository.MeditationRepository
import mx.edu.utng.raot.meditationtracker.navigation.NavigationGraph
import mx.edu.utng.raot.meditationtracker.ui.theme.MeditationTrackerTheme
import mx.edu.utng.raot.meditationtracker.ui.viewmodel.MeditationViewModel
import mx.edu.utng.raot.meditationtracker.ui.viewmodel.MeditationViewModelFactory

/**
 * MainActivity: El corazón de la aplicación Android
 *
 * Responsabilidades:
 * 1. Inicializar la base de datos
 * 2. Crear el repositorio
 * 3. Configurar el ViewModel
 * 4. Establecer el contenido de Compose
 *
 * Ciclo de vida:
 * onCreate() -> onStart() -> onResume() -> [APP VISIBLE] ->
 * onPause() -> onStop() -> onDestroy()
 */
class MainActivity : ComponentActivity() {

    /**
     * onCreate: Se llama cuando se crea la Activity
     * Solo se ejecuta UNA vez (a menos que el sistema destruya la app)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * enableEdgeToEdge: Permite que el contenido use toda la pantalla
         * (incluye el área de la barra de estado)
         *
         * Pedagogía: Diseño inmersivo moderno
         */
        enableEdgeToEdge()

        /**
         * Inicializar la base de datos
         * Singleton pattern asegura una sola instancia
         */
        val database = MeditationDatabase.getDatabase(applicationContext)
        val repository = MeditationRepository(database.meditationDao())

        /**
         * setContent: Define la UI de Compose
         * Reemplaza el tradicional setContentView(R.layout.activity_main)
         */
        setContent {
            /**
             * Aplicar el tema
             */
            MeditationTrackerTheme {
                /**
                 * Surface: Contenedor base con color de fondo
                 * Como el lienzo de un pintor
                 */
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /**
                     * Crear el ViewModel
                     * viewModel() es una función de Compose que:
                     * 1. Crea el ViewModel si no existe
                     * 2. Lo reutiliza si ya existe
                     * 3. Lo mantiene durante cambios de configuración
                     *
                     * factory: Necesario para pasar parámetros al ViewModel
                     */
                    val viewModel: MeditationViewModel = viewModel(
                        factory = MeditationViewModelFactory(repository)
                    )

                    /**
                     * Iniciar el sistema de navegación
                     */
                    NavigationGraph(viewModel = viewModel)
                }
            }
        }
    }
}