package mx.edu.utng.raot.meditationtracker.data.database

// data/database/MeditationDao.kt
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO: Define TODAS las operaciones con la base de datos
 * Es como el menú de un restaurante: lista todo lo que puedes pedir
 */
@Dao
interface MeditationDao {

    /**
     * Insertar una nueva sesión
     * @return el ID de la sesión insertada
     *
     * Ejemplo de uso: Cuando el usuario termina una meditación de 10 minutos
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: MeditationSession): Long

    /**
     * Obtener TODAS las sesiones ordenadas por fecha (más reciente primero)
     * Flow = flujo de datos reactivo (se actualiza automáticamente)
     *
     * Analogía: Como una suscripción a un canal de YouTube,
     * te notifica cuando hay contenido nuevo
     */
    @Query("SELECT * FROM meditation_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<MeditationSession>>

    /**
     * Obtener sesiones de los últimos N días
     * Útil para gráficos semanales o mensuales
     */
    @Query("""
      SELECT * FROM meditation_sessions
      WHERE date >= :startDate
      ORDER BY date DESC
      """)
    fun getSessionsSince(startDate: Long): Flow<List<MeditationSession>>

    /**
     * Obtener sesiones por tipo
     * Ejemplo: Solo meditaciones, o solo yoga
     */
    @Query("SELECT * FROM meditation_sessions WHERE type = :type ORDER BY date DESC")
    fun getSessionsByType(type: SessionType): Flow<List<MeditationSession>>

    /**
     * Obtener estadísticas: total de minutos por tipo
     * Esto alimentará nuestro gráfico de pastel
     */
    @Query("""
      SELECT type, SUM(durationMinutes) as total
      FROM meditation_sessions
      WHERE date >= :startDate
      GROUP BY type
      """)
    fun getTotalMinutesByType(startDate: Long): Flow<List<TypeDuration>>

    /**
     * Obtener minutos diarios para el gráfico de barras
     */
    @Query("""
      SELECT date, SUM(durationMinutes) as totalMinutes
      FROM meditation_sessions
      WHERE date >= :startDate
      GROUP BY DATE(date / 1000, 'unixepoch')
      ORDER BY date ASC
      """)
    fun getDailyMinutes(startDate: Long): Flow<List<DailyMinutes>>

    /**
     * Eliminar una sesión
     * Por si el usuario comete un error al registrar
     */
    @Delete
    suspend fun deleteSession(session: MeditationSession)

    /**
     * Actualizar una sesión existente
     */
    @Update
    suspend fun updateSession(session: MeditationSession)
}

/**
 * Clases auxiliares para queries específicas
 */
data class TypeDuration(
    val type: SessionType,
    val total: Int
)

data class DailyMinutes(
    val date: Long,
    val totalMinutes: Int
)