package mx.edu.utng.raot.meditationtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utng.raot.meditationtracker.data.repository.SessionStatistics

/**
 * Card que muestra estadísticas generales
 * Muestra métricas importantes de un vistazo
 *
 * Pedagogía: Este componente usa el principio de "información a primera vista"
 * Como el tablero de un auto: velocidad, gasolina, temperatura - todo visible
 */
@Composable
fun StatisticsCard(
    statistics: SessionStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📊 Esta Semana",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            /**
             * Row con distribución uniforme
             * Cada estadística ocupa el mismo espacio
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Total de sesiones
                StatisticItem(
                    value = statistics.totalSessions.toString(),
                    label = "Sesiones",
                    modifier = Modifier.weight(1f)
                )

                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                // Total de minutos
                StatisticItem(
                    value = statistics.totalMinutes.toString(),
                    label = "Minutos",
                    modifier = Modifier.weight(1f)
                )

                Divider(
                    modifier = Modifier
                        .height(60.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                // Promedio
                StatisticItem(
                    value = statistics.averageMinutes.toString(),
                    label = "Promedio",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado de ánimo promedio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado de ánimo promedio: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Mostrar estrellas según el mood
                repeat(5) { index ->
                    Text(
                        text = if (index < statistics.averageMood.toInt()) "⭐" else "☆",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Componente individual de estadística
 * Muestra un número grande con su etiqueta
 */
@Composable
private fun StatisticItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}