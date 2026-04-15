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
    // Récupération des catégories actives
    @Query("SELECT * FROM categories WHERE isActivate = 1 ORDER BY name ASC")
    fun getActivateCategories(): LiveData<List<Category>>

    // Recuperation de toutes les categories
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // recuperer categorie par id
    @Query("SELECT * FROM categories WHERE id =:id")
    fun getCategoryById(id: Int): Category?

    //Ajouter une categorie
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: Category): Long

    //Modifier categorie
    @Update
    suspend fun updateCategory(category: Category)

    //Activer ou deactiver une categorie
    @Query("UPDATE categories SET isActivate = :isActivate WHERE id = :id")
    suspend fun setCategoryActive(id: Int, isActivate: Boolean)

    //supprimer une categorie
    @Delete
    suspend fun deleteCategory(category: Category)

    // verifier si categorie a des depenses avant de supprimer
    @Query("SELECT COUNT(*) FROM expenses WHERE categoryId = :categoryID")
    suspend fun countExpensesByCategory(categoryID: Int): Int
    fun getActiveCategories(): LiveData<List<Category>>
}
