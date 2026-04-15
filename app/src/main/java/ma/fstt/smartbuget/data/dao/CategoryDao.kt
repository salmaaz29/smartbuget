package ma.fstt.smartbuget.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ma.fstt.smartbuget.data.entity.Category

@Dao
interface CategoryDao {
    // Récupération des catégories actives
    @Query("SELECT * FROM categories WHERE isActivate = 1 ORDER BY name ASC")
    fun getActivateCategories(): LiveData<List<Category>>
}
