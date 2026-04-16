package ma.fstt.smartbuget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.data.database.AppDatabase
import ma.fstt.smartbuget.data.entity.Category
import ma.fstt.smartbuget.data.repository.CategoryRepository

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository

    // Toutes les catégories actives (pour le formulaire et filtres)
    val activeCategories: LiveData<List<Category>>

    // Toutes les catégories (pour l'écran Paramètres)
    val allCategories: LiveData<List<Category>>

    // Message d'erreur (ex: suppression refusée)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val dao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(dao)
        activeCategories = repository.allActiveCategories
        allCategories = repository.allCategories
    }

    // ── CRUD ─────────────────────────────────────────────

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
            _errorMessage.value =
                "Impossible de supprimer : des dépenses utilisent cette catégorie"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}