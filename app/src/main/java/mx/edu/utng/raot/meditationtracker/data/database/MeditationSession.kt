package mx.edu.utng.raot.meditationtracker.data.database

// data/database/MeditationSession.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una sesión de meditación
 * @property id: Identificador único (como tu número de estudiante)
 * @property type: Tipo de práctica (meditación, respiración, yoga)
 * @property durationMinutes: Duración en minutos
 * @property date: Fecha y hora de la sesión
 * @property mood: Estado de ánimo después (1-5)
 * @property notes: Notas personales opcionales
 */
@Entity(tableName = "meditation_sessions")
data class MeditationSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val type: SessionType,
    val durationMinutes: Int,
    val date: Long = System.currentTimeMillis(), // Timestamp
    val mood: Int, // 1 (muy mal) a 5 (excelente)
    val notes: String = ""
)

/**
 * Tipos de sesiones disponibles
 * Enum = lista fija de opciones (como días de la semana)
 */
enum class SessionType {
    MEDITATION,      // Meditación guiada
    BREATHING,       // Ejercicios de respiración
    YOGA,           // Práctica de yoga
    JOURNALING,     // Escritura reflexiva
    GRATITUDE       // Práctica de gratitud
}