package ma.fstt.smartbuget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.data.database.AppDatabase
import ma.fstt.smartbuget.data.entity.Category
import ma.fstt.smartbuget.data.repository.CategoryRepository

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository by lazy {
        val dao = AppDatabase.getDatabase(application).categoryDao()
        CategoryRepository(dao)
    }

    val activeCategories: LiveData<List<Category>> by lazy {
        repository.allActiveCategories
    }

    val allCategories: LiveData<List<Category>> by lazy {
        repository.allCategories
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun addCategory(category: Category) = viewModelScope.launch {
        try {
            repository.insert(category)
        } catch (e: Exception) {
            _errorMessage.value = "Ce nom de catégorie existe déjà"
        }
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun toggleCategoryActive(category: Category) = viewModelScope.launch {
        repository.setActive(category.id, !category.isActivate)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        val deleted = repository.delete(category)
        if (!deleted) {
            _errorMessage.value = "Impossible de supprimer : des dépenses utilisent cette catégorie"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}