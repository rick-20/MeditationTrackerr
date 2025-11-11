package mx.edu.utng.raot.meditationtracker.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utng.raot.meditationtracker.data.database.TypeDuration
import mx.edu.utng.raot.meditationtracker.ui.theme.getColorForSessionType
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gráfico de pastel personalizado
 *
 * ¿Por qué no usar una librería?
 * - Control total sobre el diseño
 * - Aprenden a dibujar en Canvas (muy útil)
 * - Optimización para nuestras necesidades
 *
 * Matemáticas involucradas:
 * - Círculo completo = 360° = 2π radianes
 * - Cada segmento = (valor / total) * 360°
 */
@Composable
fun PieChartCard(
    data: List<TypeDuration>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        // Mostrar mensaje cuando no hay datos
        EmptyChartMessage("No hay datos suficientes para mostrar el gráfico")
        return
    }

    val total = data.sumOf { it.total }

    if (total == 0) {
        EmptyChartMessage("Aún no has registrado sesiones")
        return
    }

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
                text = "📈 Distribución de Actividades",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Últimos 30 días",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // El gráfico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    data = data,
                    total = total,
                    modifier = Modifier.size(220.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Leyenda
            Legend(data = data, total = total)
        }
    }
}

/**
 * El gráfico de pastel en sí
 * Usa Canvas para dibujar
 */
@Composable
private fun PieChart(
    data: List<TypeDuration>,
    total: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        /**
         * startAngle: Ángulo donde empezamos a dibujar
         * Empezamos en -90° (arriba del círculo)
         *
         * Analogía: Como el 12 en un reloj
         */
        var startAngle = -90f

        data.forEach { item ->
            // Calcular el ángulo de este segmento
            val sweepAngle = (item.total.toFloat() / total.toFloat()) * 360f
            val color = getColorForSessionType(item.type)

            /**
             * drawArc: Dibuja un arco (pedazo de círculo)
             *
             * Parámetros:
             * - color: Color del segmento
             * - startAngle: Dónde empezar
             * - sweepAngle: Cuántos grados dibujar
             * - useCenter: true = pastel, false = dona
             */
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // Borde blanco entre segmentos (mejor legibilidad)
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 2f)
            )

            /**
             * Dibujar el porcentaje en el centro del segmento
             * Matemáticas:
             * - Ángulo medio = startAngle + (sweepAngle / 2)
             * - Convertir a radianes: * (π / 180)
             * - X = cos(ángulo) * distancia
             * - Y = sin(ángulo) * distancia
             */
            if (sweepAngle > 20f) { // Solo si el segmento es visible
                val angle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val textRadius = radius * 0.7f // 70% del radio
                val textX = center.x + (textRadius * cos(angle)).toFloat()
                val textY = center.y + (textRadius * sin(angle)).toFloat()

                val percentage = ((item.total.toFloat() / total.toFloat()) * 100).toInt()

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        this.color = Color.White.toArgb()
                        textSize = 32f
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                    }

                    drawText(
                        "$percentage%",
                        textX,
                        textY + 12f, // Ajuste vertical
                        paint
                    )
                }
            }

            startAngle += sweepAngle
        }
    }
}

/**
 * Leyenda del gráfico
 * Muestra qué color corresponde a qué actividad
 */
@Composable
private fun Legend(
    data: List<TypeDuration>,
    total: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        data.forEach { item ->
            val percentage = ((item.total.toFloat() / total.toFloat()) * 100).toInt()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cuadro de color
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(2.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(color = getColorForSessionType(item.type))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre de la actividad
                Text(
                    text = getSessionTypeName(item.type),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                // Minutos y porcentaje
                Text(
                    text = "${item.total} min ($percentage%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = getColorForSessionType(item.type)
                )
            }
        }
    }
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

private fun getSessionTypeName(type: mx.edu.utng.raot.meditationtracker.data.database.SessionType): String {
    return when(type) {
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.MEDITATION -> "Meditación"
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.BREATHING -> "Respiración"
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.YOGA -> "Yoga"
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.JOURNALING -> "Diario"
        mx.edu.utng.raot.meditationtracker.data.database.SessionType.GRATITUDE -> "Gratitud"
    }
}