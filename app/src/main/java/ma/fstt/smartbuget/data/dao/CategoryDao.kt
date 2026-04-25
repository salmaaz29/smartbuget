package ma.fstt.smartbuget.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ma.fstt.smartbuget.data.entity.Category

@Dao
interface CategoryDao {

    // Récupérer les catégories actives (pour formulaire et liste)
    @Query("SELECT * FROM categories WHERE isActivate = 1 ORDER BY name ASC")
    fun getActiveCategories(): LiveData<List<Category>>

    // Récupérer TOUTES les catégories (pour paramètres)
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // Récupérer une catégorie par ID
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    // Ajouter une catégorie
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: Category): Long

    // Modifier une catégorie
    @Update
    suspend fun updateCategory(category: Category)

    // Activer / Désactiver une catégorie
    @Query("UPDATE categories SET isActivate = :isActive WHERE id = :id")
    suspend fun setCategoryActive(id: Int, isActive: Boolean)

    // Supprimer une catégorie
    @Delete
    suspend fun deleteCategory(category: Category)

    // Vérifier si une catégorie a des dépenses
    @Query("SELECT COUNT(*) FROM expenses WHERE categoryId = :categoryId")
    suspend fun countExpensesByCategory(categoryId: Int): Int
}