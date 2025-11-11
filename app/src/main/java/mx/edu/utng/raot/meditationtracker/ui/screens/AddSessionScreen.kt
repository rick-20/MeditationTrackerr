package mx.edu.utng.raot.meditationtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utng.raot.meditationtracker.data.database.SessionType
import mx.edu.utng.raot.meditationtracker.ui.theme.getColorForSessionType
import mx.edu.utng.raot.meditationtracker.ui.viewmodel.MeditationViewModel

/**
 * Pantalla para agregar una nueva sesión
 *
 * Principios de UX aplicados:
 * 1. Valores por defecto sensatos
 * 2. Validación en tiempo real
 * 3. Feedback visual claro
 * 4. Confirmación de éxito
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    viewModel: MeditationViewModel,
    onNavigateBack: () -> Unit
) {
    /**
     * Estados locales del formulario
     * remember: Mantiene el estado entre recomposiciones
     * mutableStateOf: Crea un estado observable
     */
    var selectedType by remember { mutableStateOf(SessionType.MEDITATION) }
    var duration by remember { mutableStateOf("10") }
    var selectedMood by remember { mutableStateOf(3) } // Neutral por defecto
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    /**
     * Observar mensajes de éxito
     * Cuando se guarda exitosamente, regresar
     */
    LaunchedEffect(message) {
        if (message?.contains("exitosamente") == true) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Sesión") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            /**
             * Sección 1: Tipo de sesión
             * Chips horizontales para selección visual
             */
            Text(
                text = "Tipo de Práctica",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            /**
             * FlowRow simula un wrap de elementos
             * (No está disponible nativamente, usaremos Column con Rows)
             */
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SessionTypeChip(
                        type = SessionType.MEDITATION,
                        isSelected = selectedType == SessionType.MEDITATION,
                        onClick = { selectedType = SessionType.MEDITATION }
                    )
                    SessionTypeChip(
                        type = SessionType.BREATHING,
                        isSelected = selectedType == SessionType.BREATHING,
                        onClick = { selectedType = SessionType.BREATHING }
                    )
                    SessionTypeChip(
                        type = SessionType.YOGA,
                        isSelected = selectedType == SessionType.YOGA,
                        onClick = { selectedType = SessionType.YOGA }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SessionTypeChip(
                        type = SessionType.JOURNALING,
                        isSelected = selectedType == SessionType.JOURNALING,
                        onClick = { selectedType = SessionType.JOURNALING }
                    )
                    SessionTypeChip(
                        type = SessionType.GRATITUDE,
                        isSelected = selectedType == SessionType.GRATITUDE,
                        onClick = { selectedType = SessionType.GRATITUDE }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /**
             * Sección 2: Duración
             * TextField con validación numérica
             */
            Text(
                text = "Duración (minutos)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = duration,
                onValueChange = { newValue ->
                    // Validar que solo sean números
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        duration = newValue
                        showError = false
                    }
                },
                label = { Text("Minutos") },
                placeholder = { Text("Ej: 15") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError,
                supportingText = if (showError) {
                    { Text(errorMessage) }
                } else null
            )

            /**
             * Botones rápidos de duración
             * UX: Reducir fricción con opciones comunes
             */
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickDurationButton(5) { duration = "5" }
                QuickDurationButton(10) { duration = "10" }
                QuickDurationButton(15) { duration = "15" }
                QuickDurationButton(20) { duration = "20" }
                QuickDurationButton(30) { duration = "30" }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /**
             * Sección 3: Estado de ánimo
             * Selector visual con emojis
             */
            Text(
                text = "¿Cómo te sientes?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            /**
             * Sección 4: Notas opcionales
             */
            Text(
                text = "Notas (opcional)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Reflexiones") },
                placeholder = { Text("¿Cómo fue tu práctica?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(32.dp))

            /**
             * Botón de guardar
             */
            Button(
                onClick = {
                    // Validar antes de guardar
                    val durationInt = duration.toIntOrNull()

                    when {
                        duration.isEmpty() -> {
                            showError = true
                            errorMessage = "Ingresa la duración"
                        }
                        durationInt == null || durationInt <= 0 -> {
                            showError = true
                            errorMessage = "La duración debe ser mayor a 0"
                        }
                        durationInt > 180 -> {
                            showError = true
                            errorMessage = "La duración máxima es 180 minutos"
                        }
                        else -> {
                            viewModel.insertSession(
                                type = selectedType,
                                duration = durationInt,
                                mood = selectedMood,
                                notes = notes.trim()
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Guardar Sesión",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Chip para seleccionar tipo de sesión
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTypeChip(
    type: SessionType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = when(type) {
                    SessionType.MEDITATION -> "🧘 Meditación"
                    SessionType.BREATHING -> "🌬️ Respiración"
                    SessionType.YOGA -> "🤸 Yoga"
                    SessionType.JOURNALING -> "📓 Diario"
                    SessionType.GRATITUDE -> "💖 Gratitud"
                }
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = getColorForSessionType(type).copy(alpha = 0.3f),
            selectedLabelColor = getColorForSessionType(type)
        )
    )
}

/**
 * Botón rápido de duración
 */
@Composable
private fun RowScope.QuickDurationButton(
    minutes: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f)
    ) {
        Text("$minutes")
    }
}


/**
 * Selector de estado de ánimo
 * Interfaz visual intuitiva con emojis
 */
@Composable
private fun MoodSelector(
    selectedMood: Int,
    onMoodSelected: (Int) -> Unit
) {
    /**
     * Datos de cada mood
     * Pedagogía: Estructura de datos clara
     * Cada mood tiene: valor, emoji, etiqueta y color
     */
    val moods = listOf(
        MoodData(1, "😢", "Muy mal", mx.edu.utng.raot.meditationtracker.ui.theme.MoodVeryBad),
        MoodData(2, "😕", "Mal", mx.edu.utng.raot.meditationtracker.ui.theme.MoodBad),
        MoodData(3, "😐", "Normal", mx.edu.utng.raot.meditationtracker.ui.theme.MoodNeutral),
        MoodData(4, "😊", "Bien", mx.edu.utng.raot.meditationtracker.ui.theme.MoodGood),
        MoodData(5, "😄", "Excelente", mx.edu.utng.raot.meditationtracker.ui.theme.MoodExcellent)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        moods.forEach { mood ->
            MoodButton(
                moodData = mood,
                isSelected = selectedMood == mood.value,
                onClick = { onMoodSelected(mood.value) }
            )
        }
    }
}

/**
 * Data class para mood
 */
private data class MoodData(
    val value: Int,
    val emoji: String,
    val label: String,
    val color: androidx.compose.ui.graphics.Color
)

/**
 * Botón individual de mood
 * Se agranda cuando está seleccionado (feedback visual)
 */
@Composable
private fun MoodButton(
    moodData: MoodData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /**
         * FilledTonalButton si está seleccionado, OutlinedButton si no
         * Principio de diseño: Estado visual claro
         */
        if (isSelected) {
            FilledTonalButton(
                onClick = onClick,
                modifier = Modifier.size(64.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = moodData.color.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = moodData.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        } else {
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.size(56.dp)
            ) {
                Text(
                    text = moodData.emoji,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = moodData.label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected)
                moodData.color
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}