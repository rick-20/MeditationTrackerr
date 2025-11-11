package mx.edu.utng.raot.meditationtracker.ui.viewmodel

// ui/viewmodel/MeditationViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mx.edu.utng.raot.meditationtracker.data.database.MeditationSession
import mx.edu.utng.raot.meditationtracker.data.repository.MeditationRepository
import mx.edu.utng.raot.meditationtracker.data.repository.SessionStatistics
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mx.edu.utng.raot.meditationtracker.data.database.SessionType

/**
 * ViewModel principal de la aplicación
 *
 * Responsabilidades:
 * 1. Exponer datos a la UI de forma reactiva (StateFlow)
 * 2. Manejar eventos del usuario (clicks, inputs)
 * 3. Coordinar operaciones con el repositorio
 * 4. Mantener el estado de la UI
 *
 * NO debe:
 * - Referenciar Views directamente
 * - Contener Context de Android
 * - Tener lógica de UI (colores, layouts, etc.)
 */
class MeditationViewModel(
    private val repository: MeditationRepository
) : ViewModel() {

    /**
     * StateFlow: Flujo de estado observable
     * La UI se suscribe y se actualiza automáticamente
     *
     * Analogía: Como un tablero de aeropuerto que muestra vuelos en tiempo real
     */

// Lista de todas las sesiones
    val allSessions: StateFlow<List<MeditationSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Se mantiene activo 5s después de que la UI se desuscribe
            initialValue = emptyList()
        )

    // Sesiones de la semana
    val weeklySessions: StateFlow<List<MeditationSession>> = repository.getWeeklySessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Datos para gráfico de pastel (últimos 30 días)
    val pieChartData = repository.getPieChartData(30)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Datos para gráfico de barras (últimos 7 días)
    val barChartData = repository.getBarChartData(7)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estadísticas calculadas
    private val _statistics = MutableStateFlow<SessionStatistics?>(null)
    val statistics: StateFlow<SessionStatistics?> = _statistics.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensajes para el usuario (éxito, error)
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
// Calcular estadísticas al iniciar
        calculateStatistics()
    }

    /**
     * Insertar una nueva sesión
     *
     * Ejemplo de uso desde la UI:
     * viewModel.insertSession(
     *     type = SessionType.MEDITATION,
     *     duration = 20,
     *     mood = 5,
     *     notes = "Excelente sesión matutina"
     * )
     */
    fun insertSession(
        type: SessionType,
        duration: Int,
        mood: Int,
        notes: String = ""
    ) {
        // Validaciones
        if (duration <= 0) {
            _message.value = "La duración debe ser mayor a 0"
            return
        }

        if (mood !in 1..5) {
            _message.value = "El estado de ánimo debe estar entre 1 y 5"
            return
        }

        // Operación asíncrona
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val session = MeditationSession(
                    type = type,
                    durationMinutes = duration,
                    mood = mood,
                    notes = notes
                )

                val id = repository.insertSession(session)

                _message.value = "✓ Sesión guardada exitosamente"
                calculateStatistics() // Recalcular estadísticas

            } catch (e: Exception) {
                _message.value = "Error al guardar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Eliminar una sesión
     */
    fun deleteSession(session: MeditationSession) {
        viewModelScope.launch {
            try {
                repository.deleteSession(session)
                _message.value = "Sesión eliminada"
                calculateStatistics()
            } catch (e: Exception) {
                _message.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    /**
     * Calcular estadísticas generales
     * Se ejecuta automáticamente cuando cambian las sesiones
     */
    private fun calculateStatistics() {
        viewModelScope.launch {
            weeklySessions.value.let { sessions ->
                if (sessions.isNotEmpty()) {
                    _statistics.value = repository.getStatistics(sessions)
                }
            }
        }
    }

    /**
     * Limpiar mensaje después de mostrarlo
     */
    fun clearMessage() {
        _message.value = null
    }

    /**
     * Obtener sesiones filtradas por tipo
     */
    fun getSessionsByType(type: SessionType): Flow<List<MeditationSession>> {
        return repository.getSessionsByType(type)
    }
}