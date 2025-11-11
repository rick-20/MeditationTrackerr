package mx.edu.utng.raot.meditationtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utng.raot.meditationtracker.data.database.DailyMinutes
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb

/**
 * Gráfico de barras para mostrar progreso diario
 *
 * Pedagogía: Este gráfico responde la pregunta:
 * "¿Cuánto he meditado cada día de la semana?"
 *
 * Conceptos matemáticos:
 * - Escala: Convertir minutos a altura de barra
 * - Proporción: La barra más alta = valor máximo
 * - Espaciado: Distribuir barras uniformemente
 */
@Composable
fun BarChartCard(
    data: List<DailyMinutes>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        EmptyChartMessage("No hay datos para mostrar en el gráfico de barras")
        return
    }

// Completar días faltantes con 0 minutos
    val completeData = fillMissingDays(data, 7)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📊 Progreso Semanal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Minutos diarios de práctica",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // El gráfico de barras
            BarChart(
                data = completeData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

/**
 * Componente del gráfico de barras
 * Dibuja las barras y etiquetas usando Canvas
 */
@Composable
private fun BarChart(
    data: List<DailyMinutes>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier.padding(16.dp)) {
        /**
         * Configuración del gráfico
         */
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Espacio para etiquetas
        val labelSpace = 60f
        val chartHeight = canvasHeight - labelSpace

        // Encontrar el valor máximo para escalar
        val maxValue = data.maxOfOrNull { it.totalMinutes }?.toFloat() ?: 1f
        val safeMaxValue = if (maxValue > 0) maxValue else 1f

        // Calcular dimensiones de las barras
        val barCount = data.size
        val totalBarSpacing = canvasWidth * 0.2f // 20% del ancho para espacios
        val spaceBetweenBars = totalBarSpacing / (barCount + 1)
        val barWidth = (canvasWidth - totalBarSpacing) / barCount

        /**
         * Dibujar líneas de cuadrícula (guías horizontales)
         * Ayudan a leer valores aproximados
         */
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = (chartHeight / gridLines) * i

            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f
            )

            // Etiqueta de valor
            val value = (safeMaxValue * (gridLines - i) / gridLines).toInt()
            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    color = Color.Gray.toArgb()
                    textSize = 28f
                    textAlign = Paint.Align.LEFT
                }
                drawText(
                    "$value",
                    10f,
                    y + 10f,
                    paint
                )
            }
        }

        /**
         * Dibujar las barras
         */
        data.forEachIndexed { index, dailyMinutes ->
            val x = spaceBetweenBars + (index * (barWidth + spaceBetweenBars))

            // Calcular altura de la barra
            // Proporción: (valor / máximo) * altura disponible
            val barHeight = if (safeMaxValue > 0) {
                (dailyMinutes.totalMinutes.toFloat() / safeMaxValue) * chartHeight * 0.9f
            } else {
                0f
            }

            val barY = chartHeight - barHeight

            /**
             * Dibujar la barra con esquinas redondeadas
             * Color más intenso si hay actividad
             */
            val barColor = if (dailyMinutes.totalMinutes > 0) {
                primaryColor
            } else {
                Color.Gray.copy(alpha = 0.3f)
            }

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, barY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )

            /**
             * Dibujar el valor encima de la barra (si hay espacio)
             */
            if (dailyMinutes.totalMinutes > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        color = primaryColor.toArgb()
                        textSize = 32f
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                    }

                    drawText(
                        "${dailyMinutes.totalMinutes}",
                        x + (barWidth / 2),
                        barY - 10f,
                        paint
                    )
                }
            }

            /**
             * Etiqueta del día (eje X)
             * Muestra día de la semana abreviado
             */
            val dayLabel = getDayLabel(dailyMinutes.date)
            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    color = Color.DarkGray.toArgb()
                    textSize = 32f
                    textAlign = Paint.Align.CENTER
                }

                drawText(
                    dayLabel,
                    x + (barWidth / 2),
                    canvasHeight - 10f,
                    paint
                )
            }
        }
    }
}

/**
 * Llenar días faltantes con 0 minutos
 * Asegura que siempre mostremos 7 días completos
 *
 * Pedagogía: Como un calendario, queremos ver TODOS los días,
 * incluso aquellos donde no hubo actividad
 */
private fun fillMissingDays(data: List<DailyMinutes>, days: Int): List<DailyMinutes> {
    val calendar = Calendar.getInstance()
    val today = calendar.timeInMillis

    // Crear un mapa de los datos existentes
    val dataMap = data.associateBy {
        normalizeDate(it.date)
    }

    // Generar lista completa de días
    val result = mutableListOf<DailyMinutes>()

    for (i in (days - 1) downTo 0) {
        calendar.timeInMillis = today
        calendar.add(Calendar.DAY_OF_YEAR, -i)
        val normalizedDate = normalizeDate(calendar.timeInMillis)

        // Si existe dato para ese día, usarlo; si no, crear uno con 0
        result.add(
            dataMap[normalizedDate] ?: DailyMinutes(
                date = normalizedDate,
                totalMinutes = 0
            )
        )
    }

    return result
}

/**
 * Normalizar fecha al inicio del día (00:00:00)
 * Necesario para comparar días sin importar la hora
 */
private fun normalizeDate(timestamp: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

/**
 * Obtener etiqueta del día (Lun, Mar, Mié, etc.)
 */
private fun getDayLabel(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    return sdf.format(Date(timestamp)).take(3) // Primeras 3 letras
}

@Composable
private fun EmptyChartMessage(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}