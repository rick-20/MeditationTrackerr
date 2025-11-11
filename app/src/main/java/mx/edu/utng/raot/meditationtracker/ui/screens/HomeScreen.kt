package mx.edu.utng.raot.meditationtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utng.raot.meditationtracker.data.database.MeditationSession
import mx.edu.utng.raot.meditationtracker.ui.components.*
import mx.edu.utng.raot.meditationtracker.ui.viewmodel.MeditationViewModel

/**
 * Pantalla principal de la aplicación
 *
 * Estructura:
 * 1. Top Bar (barra superior)
 * 2. Estadísticas generales
 * 3. Gráficos (pastel y barras)
 * 4. Lista de sesiones recientes
 * 5. FAB (botón flotante) para agregar sesión
 *
 * Pedagogía: Arquitectura de información
 * - Lo más importante arriba (estadísticas)
 * - Visualizaciones en medio (gráficos)
 * - Detalles abajo (lista de sesiones)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MeditationViewModel,
    onAddSessionClick: () -> Unit
) {
    /**
     * Observar estados del ViewModel
     * collectAsState() convierte Flow en State de Compose
     *
     * Analogía: Como suscribirte a un canal de YouTube
     * Te notifica automáticamente de nuevos videos
     */
    val sessions by viewModel.weeklySessions.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val pieChartData by viewModel.pieChartData.collectAsState()
    val barChartData by viewModel.barChartData.collectAsState()
    val message by viewModel.message.collectAsState()

    /**
     * Scaffold: Estructura básica de Material Design
     * Proporciona slots para: TopBar, FAB, BottomBar, etc.
     *
     * Analogía: Como el esqueleto de un edificio
     */
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🧘 Meditación",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Tu viaje de autocuidado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            /**
             * FAB: Floating Action Button
             * La acción principal de la pantalla
             *
             * Principio de diseño: "Una acción, un botón"
             */
            FloatingActionButton(
                onClick = onAddSessionClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar sesión"
                )
            }
        }
    ) { paddingValues ->
        /**
         * LazyColumn: Lista eficiente (como RecyclerView)
         * Solo renderiza elementos visibles
         *
         * Pedagogía: Optimización
         * Si tienes 1000 sesiones, no crea 1000 vistas
         * Solo crea las que ves en pantalla (~10)
         */
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para FAB
        ) {
            /**
             * Sección de estadísticas
             */
            item {
                statistics?.let { stats ->
                    StatisticsCard(statistics = stats)
                } ?: run {
                    // Placeholder mientras cargan las estadísticas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            /**
             * Gráfico de pastel
             */
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PieChartCard(data = pieChartData)
            }

            /**
             * Gráfico de barras
             */
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BarChartCard(data = barChartData)
            }

            /**
             * Encabezado de sesiones recientes
             */
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "📝 Sesiones Recientes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            /**
             * Lista de sesiones
             * items() es una función especial de LazyColumn
             * Itera sobre una lista y crea items dinámicamente
             */
            if (sessions.isEmpty()) {
                item {
                    EmptySessionsMessage()
                }
            } else {
                items(
                    items = sessions,
                    key = { it.id } // Importante para performance
                ) { session ->
                    SessionCard(
                        session = session,
                        onDelete = { viewModel.deleteSession(it) }
                    )
                }
            }
        }
    }

    /**
     * Mostrar mensajes (Snackbar)
     * LaunchedEffect se ejecuta cuando 'message' cambia
     *
     * Pedagogía: Side effects
     * Acciones que ocurren FUERA de la composición
     * (mostrar diálogos, navegar, etc.)
     */
    message?.let { msg ->
        LaunchedEffect(msg) {
            // En una app real, mostrarías un Snackbar aquí
            viewModel.clearMessage()
        }
    }
}

/**
 * Mensaje cuando no hay sesiones
 */
@Composable
private fun EmptySessionsMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌱",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Comienza tu viaje",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega tu primera sesión de meditación usando el botón +",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}