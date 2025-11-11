package mx.edu.utng.raot.meditationtracker.util

// util/TestDataHelper.kt

import mx.edu.utng.raot.meditationtracker.data.database.MeditationSession
import mx.edu.utng.raot.meditationtracker.data.database.SessionType
import mx.edu.utng.raot.meditationtracker.data.repository.MeditationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Helper para generar datos de prueba
 * Útil durante el desarrollo
 *
 * IMPORTANTE: Eliminar o deshabilitar en producción
 */
object TestDataHelper {

    /**
     * Insertar datos de ejemplo
     * Crea 14 días de sesiones con variedad
     */
    fun insertSampleData(repository: MeditationRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            val calendar = Calendar.getInstance()

            // Generar sesiones para los últimos 14 días
            for (daysAgo in 0..13) {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

                // Algunas días tienen múltiples sesiones
                val sessionsToday = when {
                    daysAgo % 3 == 0 -> 2 // Cada 3 días, 2 sesiones
                    daysAgo % 7 == 0 -> 0 // Cada semana, día de descanso
                    else -> 1
                }

                repeat(sessionsToday) { sessionIndex ->
                    // Variar la hora del día
                    calendar.set(Calendar.HOUR_OF_DAY, 8 + (sessionIndex * 8))

                    val session = MeditationSession(
                        type = getRandomSessionType(),
                        durationMinutes = getRandomDuration(),
                        date = calendar.timeInMillis,
                        mood = (3..5).random(), // Moods positivos mayormente
                        notes = getSampleNote()
                    )

                    repository.insertSession(session)
                }
            }
        }
    }

    private fun getRandomSessionType(): SessionType {
        return SessionType.values().random()
    }

    private fun getRandomDuration(): Int {
        val commonDurations = listOf(5, 10, 15, 20, 30)
        return commonDurations.random()
    }

    private fun getSampleNote(): String {
        val notes = listOf(
            "Sesión muy relajante",
            "Me sentí más centrado",
            "Difícil concentrarme hoy",
            "Excelente práctica matutina",
            "Necesitaba esto después del trabajo",
            ""
        )
        return notes.random()
    }
}
//Para usar los datos de prueba (agregar en MainActivity, solo durante desarrollo):
//kotlin// En MainActivity.onCreate(), después de crear el repository:
// SOLO PARA DESARROLLO - COMENTAR EN PRODUCCIÓN
// TestDataHelper.insertSampleData(repository)