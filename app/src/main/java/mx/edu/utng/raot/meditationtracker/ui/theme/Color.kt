// ui/theme/Color.kt
package mx.edu.utng.raot.meditationtracker.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de colores para la app de meditación
 * Colores inspirados en la naturaleza y la tranquilidad
 */

// Colores primarios (tonos violeta/lavanda - asociados con espiritualidad)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Colores para tipos de sesiones (importante para gráficos)
val MeditationColor = Color(0xFF9C27B0) // Violeta profundo
val BreathingColor = Color(0xFF2196F3)  // Azul cielo
val YogaColor = Color(0xFF4CAF50)       // Verde naturaleza
val JournalingColor = Color(0xFFFF9800) // Naranja cálido
val GratitudeColor = Color(0xFFE91E63)  // Rosa suave

// Colores para estados de ánimo (usados en indicadores)
val MoodVeryBad = Color(0xFFD32F2F)   // Rojo
val MoodBad = Color(0xFFFF6F00)       // Naranja oscuro
val MoodNeutral = Color(0xFFFFC107)   // Amarillo
val MoodGood = Color(0xFF689F38)      // Verde claro
val MoodExcellent = Color(0xFF388E3C) // Verde brillante

// Función helper para obtener color por tipo de sesión
fun getColorForSessionType(type: mx.edu.utng.raot.meditationtracker.data.database.SessionType): Color {
    return when(type) {
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.MEDITATION -> MeditationColor
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.BREATHING -> BreathingColor
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.YOGA -> YogaColor
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.JOURNALING -> JournalingColor
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.GRATITUDE -> GratitudeColor
    }
}

// Función para obtener color por mood
fun getColorForMood(mood: Int): Color {
    return when(mood) {
        1 -> MoodVeryBad
        2 -> MoodBad
        3 -> MoodNeutral
        4 -> MoodGood
        5 -> MoodExcellent
        else -> MoodNeutral
    }
}