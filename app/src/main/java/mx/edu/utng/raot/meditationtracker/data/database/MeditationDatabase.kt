package mx.edu.utng.raot.meditationtracker.data.database

// data/database/MeditationDatabase.kt
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Base de datos principal de la aplicación
 *
 * @Database: Anotación que define:
 * - entities: Qué tablas tiene (como salas en un edificio)
 * - version: Número de versión (si cambias estructura, aumenta esto)
 * - exportSchema: Guardar historial de cambios (buena práctica)
 */
@Database(
    entities = [MeditationSession::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MeditationDatabase : RoomDatabase() {

    /**
     * Función abstracta que Room implementa automáticamente
     * Retorna nuestro DAO (el bibliotecario)
     */
    abstract fun meditationDao(): MeditationDao

    companion object {
        /**
         * Singleton: Solo una instancia de la base de datos
         * Analogía: Solo hay UNA biblioteca central, no una por persona
         *
         * @Volatile: Asegura que todos los threads vean el mismo valor
         */
        @Volatile
        private var INSTANCE: MeditationDatabase? = null

        /**
         * Obtener la instancia de la base de datos
         * Synchronized: Solo un thread puede crear la instancia a la vez
         *
         * Analogía: Como formar fila, uno a la vez
         */
        fun getDatabase(context: Context): MeditationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MeditationDatabase::class.java,
                    "meditation_database" // Nombre del archivo
                )
                    // Estrategias opcionales:
                    // .fallbackToDestructiveMigration() // Si hay error, borra todo (solo en desarrollo)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Converters: Para tipos de datos que Room no entiende nativamente
 * Room solo entiende tipos primitivos (Int, String, Long, etc.)
 * Necesitamos convertir Date y Enum a tipos que Room entienda
 */
class Converters {

    /**
     * Convertir SessionType (enum) a String para guardar en BD
     */
    @TypeConverter
    fun fromSessionType(type: SessionType): String {
        return type.name
    }

    /**
     * Convertir String a SessionType al leer de BD
     */
    @TypeConverter
    fun toSessionType(value: String): SessionType {
        return SessionType.valueOf(value)
    }
}