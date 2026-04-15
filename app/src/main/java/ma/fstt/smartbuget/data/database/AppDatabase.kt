package ma.fstt.smartbuget.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.data.dao.CategoryDao
import ma.fstt.smartbuget.data.dao.ExpenseDao
import ma.fstt.smartbuget.data.entity.Category
import ma.fstt.smartbuget.data.entity.Expense


@Database(entities = [Category::class, Expense::class], version = 1, exportSchema = false)


abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartbudget_database"
                )
                    .addCallback(DatabaseCallback()) // ← insère les données par défaut
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback : insérer les catégories par défaut au 1er lancement
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.categoryDao())
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                Category(name = "Alimentation", icon = "🍔", color = "#FF5722"),
                Category(name = "Transport",    icon = "🚌", color = "#2196F3"),
                Category(name = "Logement",     icon = "🏠", color = "#4CAF50"),
                Category(name = "Santé",        icon = "💊", color = "#E91E63"),
                Category(name = "Loisirs",      icon = "🎮", color = "#9C27B0"),
                Category(name = "Études",       icon = "📚", color = "#FF9800"),
                Category(name = "Autre",        icon = "📦", color = "#607D8B")
            )
            defaultCategories.forEach { categoryDao.insertCategory(it) }
        }
    }
}