package ma.fstt.smartbuget.data.repository

import androidx.lifecycle.LiveData
import ma.fstt.smartbuget.data.dao.CategoryDao
import ma.fstt.smartbuget.data.entity.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allActiveCategories: LiveData<List<Category>> = categoryDao.getActiveCategories()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(category: Category) = categoryDao.insertCategory(category)

    suspend fun update(category: Category) = categoryDao.updateCategory(category)

    suspend fun setActive(id: Int, isActive: Boolean) =
        categoryDao.setCategoryActive(id, isActive)

    suspend fun delete(category: Category): Boolean {
        val count = categoryDao.countExpensesByCategory(category.id)
        return if (count == 0) {
            categoryDao.deleteCategory(category)
            true
        } else {
            false // suppression refusée, des dépenses existent
        }
    }
}