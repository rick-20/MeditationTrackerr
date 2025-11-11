package mx.edu.utng.raot.meditationtracker.data.repository

// data/repository/MeditationRepository.kt
import mx.edu.utng.raot.meditationtracker.data.database.*
import kotlinx.coroutines.flow.Flow
import mx.edu.utng.raot.meditationtracker.data.database.DailyMinutes
import mx.edu.utng.raot.meditationtracker.data.database.MeditationDao
import mx.edu.utng.raot.meditationtracker.data.database.MeditationSession
import mx.edu.utng.raot.meditationtracker.data.database.SessionType
import mx.edu.utng.raot.meditationtracker.data.database.TypeDuration
import java.util.Calendar

/**
 * Repositorio: Única fuente de verdad para los datos
 *
 * Ventajas de usar un repositorio:
 * 1. Abstracción: La UI no sabe de dónde vienen los datos (BD, API, caché)
 * 2. Testing: Fácil de probar con datos falsos
 * 3. Mantenibilidad: Si cambias la BD, solo cambias aquí
 *
 * Analogía: Como Amazon, tú pides productos sin saber de qué bodega vienen
 */
class MeditationRepository(private val dao: MeditationDao) {

    /**
     * Obtener todas las sesiones
     * Flow permite que la UI se actualice automáticamente
     */
    val allSessions: Flow<List<MeditationSession>> = dao.getAllSessions()

    /**
     * Insertar una nueva sesión
     *
     * @param session: La sesión a guardar
     * @return El ID generado
     *
     * Ejemplo de uso:
     * val id = repository.insertSession(
     *     MeditationSession(
     *         type = SessionType.MEDITATION,
     *         durationMinutes = 15,
     *         mood = 4,
     *         notes = "Me sentí tranquilo"
     *     )
     * )
     */
    suspend fun insertSession(session: MeditationSession): Long {
        return dao.insertSession(session)
    }

    /**
     * Obtener sesiones de la última semana
     * Útil para mostrar progreso reciente
     */
    fun getWeeklySessions(): Flow<List<MeditationSession>> {
        val weekAgo = getDateDaysAgo(7)
        return dao.getSessionsSince(weekAgo)
    }

    /**
     * Obtener sesiones del último mes
     * Para estadísticas mensuales
     */
    fun getMonthlySessions(): Flow<List<MeditationSession>> {
        val monthAgo = getDateDaysAgo(30)
        return dao.getSessionsSince(monthAgo)
    }

    /**
     * Obtener datos para gráfico de pastel
     * Muestra distribución de tiempo por tipo de práctica
     *
     * Ejemplo de resultado:
     * - Meditación: 120 minutos (50%)
     * - Yoga: 80 minutos (33%)
     * - Respiración: 40 minutos (17%)
     */
    fun getPieChartData(days: Int = 30): Flow<List<TypeDuration>> {
        val startDate = getDateDaysAgo(days)
        return dao.getTotalMinutesByType(startDate)
    }

    /**
     * Obtener datos para gráfico de barras
     * Muestra minutos diarios de práctica
     *
     * Ejemplo de resultado:
     * - Lunes: 20 minutos
     * - Martes: 15 minutos
     * - Miércoles: 0 minutos
     * - etc.
     */
    fun getBarChartData(days: Int = 7): Flow<List<DailyMinutes>> {
        val startDate = getDateDaysAgo(days)
        return dao.getDailyMinutes(startDate)
    }

    /**
     * Eliminar una sesión
     */
    suspend fun deleteSession(session: MeditationSession) {
        dao.deleteSession(session)
    }

    /**
     * Actualizar una sesión existente
     */
    suspend fun updateSession(session: MeditationSession) {
        dao.updateSession(session)
    }

    /**
     * Obtener sesiones por tipo específico
     */
    fun getSessionsByType(type: SessionType): Flow<List<MeditationSession>> {
        return dao.getSessionsByType(type)
    }

    /**
     * Calcular estadísticas generales
     * Retorna un objeto con métricas útiles
     */
    suspend fun getStatistics(sessions: List<MeditationSession>): SessionStatistics {
        return SessionStatistics(
            totalSessions = sessions.size,
            totalMinutes = sessions.sumOf { it.durationMinutes },
            averageMinutes = if (sessions.isNotEmpty())
                sessions.sumOf { it.durationMinutes } / sessions.size
            else 0,
            averageMood = if (sessions.isNotEmpty())
                sessions.sumOf { it.mood }.toFloat() / sessions.size
            else 0f,
            mostFrequentType = sessions
                .groupBy { it.type }
                .maxByOrNull { it.value.size }
                ?.key ?: SessionType.MEDITATION
        )
    }

    /**
     * Función auxiliar: Obtener fecha hace X días
     * Útil para filtrar por rangos de tiempo
     */
    private fun getDateDaysAgo(days: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

/**
 * Data class para estadísticas
 * Agrupa métricas útiles en un solo objeto
 */
data class SessionStatistics(
    val totalSessions: Int,
    val totalMinutes: Int,
    val averageMinutes: Int,
    val averageMood: Float,
    val mostFrequentType: SessionType
)