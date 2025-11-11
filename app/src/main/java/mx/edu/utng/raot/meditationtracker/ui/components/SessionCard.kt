package mx.edu.utng.raot.meditationtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.edu.utng.raot.meditationtracker.data.database.MeditationSession
import mx.edu.utng.raot.meditationtracker.data.database.SessionType
import mx.edu.utng.raot.meditationtracker.ui.theme.getColorForMood
import mx.edu.utng.raot.meditationtracker.ui.theme.getColorForSessionType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card que muestra una sesión de meditación
 * Componente reutilizable en diferentes pantallas
 *
 * @param session: Los datos de la sesión
 * @param onDelete: Función que se ejecuta al eliminar (opcional)
 *
 * Ejemplo de uso:
 * SessionCard(
 *     session = mySession,
 *     onDelete = { viewModel.deleteSession(it) }
 * )
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionCard(
    session: MeditationSession,
    onDelete: ((MeditationSession) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    /**
     * Card: Contenedor con elevación (sombra)
     * Es como una tarjeta física que se ve elevada sobre la mesa
     */
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        /**
         * Row: Organiza elementos horizontalmente
         * Column: Organiza elementos verticalmente
         *
         * Analogía: Row es como una fila de estudiantes, Column es como una torre de bloques
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del tipo de sesión
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getColorForSessionType(session.type).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getIconForSessionType(session.type),
                    contentDescription = session.type.name,
                    tint = getColorForSessionType(session.type),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Tipo de sesión
                Text(
                    text = getSessionTypeName(session.type),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Duración y fecha
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Duración",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session.durationMinutes} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(session.date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Notas (si existen)
                if (session.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = session.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Indicador de estado de ánimo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = getColorForMood(session.mood),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getMoodEmoji(session.mood),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Botón eliminar (si se proporciona la función)
                if (onDelete != null) {
                    IconButton(
                        onClick = { onDelete(session) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Funciones helper para convertir datos a texto legible
 */

private fun getSessionTypeName(type: SessionType): String {
    return when(type) {
        SessionType.MEDITATION -> "Meditación"
        SessionType.BREATHING -> "Respiración"
        SessionType.YOGA -> "Yoga"
        SessionType.JOURNALING -> "Diario"
        SessionType.GRATITUDE -> "Gratitud"
    }
}

private fun getIconForSessionType(type: SessionType): ImageVector {
    return when(type) {
        SessionType.MEDITATION -> Icons.Default.SelfImprovement
        SessionType.BREATHING -> Icons.Default.Air
        SessionType.YOGA -> Icons.Default.FitnessCenter
        SessionType.JOURNALING -> Icons.Default.MenuBook
        SessionType.GRATITUDE -> Icons.Default.Favorite
    }
}

private fun getMoodEmoji(mood: Int): String {
    return when(mood) {
        1 -> "😢"
        2 -> "😕"
        3 -> "😐"
        4 -> "😊"
        5 -> "😄"
        else -> "😐"
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MMM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}