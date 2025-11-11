package mx.edu.utng.raot.meditationtracker.ui.viewmodel

// ui/viewmodel/MeditationViewModelFactory.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.utng.raot.meditationtracker.data.repository.MeditationRepository

/**
 * Factory para crear ViewModels con parámetros
 *
 * ¿Por qué necesitamos esto?
 * - ViewModel normalmente se crea sin parámetros
 * - Nuestro ViewModel necesita el repositorio
 * - Factory permite inyectar dependencias
 *
 * Analogía: Como pedir una hamburguesa personalizada
 * Le dices al chef qué ingredientes quieres
 */
class MeditationViewModelFactory(
    private val repository: MeditationRepository
) : ViewModelProvider.Factory {

    /**
     * create: Método que Android llama para crear el ViewModel
     *
     * @Suppress("UNCHECKED_CAST"): Le decimos al compilador
     * "confía en mí, sé lo que hago"
     *
     * Pedagogía: Type casting
     * Convertimos un tipo genérico a nuestro tipo específico
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeditationViewModel::class.java)) {
            return MeditationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}