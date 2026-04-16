package ma.fstt.smartbuget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.data.database.AppDatabase
import ma.fstt.smartbuget.data.entity.Expense
import ma.fstt.smartbuget.data.repository.ExpenseRepository
import java.util.*

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    // Mois actuellement affiché (Calendar)
    private val _currentCalendar = MutableLiveData(Calendar.getInstance())
    val currentCalendar: LiveData<Calendar> = _currentCalendar

    // Catégorie filtrée (null = toutes)
    private val _selectedCategoryId = MutableLiveData<Int?>(null)
    val selectedCategoryId: LiveData<Int?> = _selectedCategoryId

    // Dépenses du mois en cours
    val expenses: LiveData<List<Expense>>

    // Total du mois
    val totalByMonth: LiveData<Double>

    init {
        val dao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)

        // Calcule start/end du mois courant
        val (start, end) = getMonthRange(Calendar.getInstance())

        expenses = repository.getExpensesByMonth(start, end)
        totalByMonth = repository.getTotalByMonth(start, end)
    }

    // ── Navigation mois ──────────────────────────────────

    fun goToPreviousMonth() {
        val cal = _currentCalendar.value ?: return
        cal.add(Calendar.MONTH, -1)
        _currentCalendar.value = cal
    }

    fun goToNextMonth() {
        val cal = _currentCalendar.value ?: return
        cal.add(Calendar.MONTH, 1)
        _currentCalendar.value = cal
    }

    // ── Filtre catégorie ─────────────────────────────────

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    // ── CRUD ─────────────────────────────────────────────

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    suspend fun getExpenseById(id: Int): Expense? {
        return repository.getById(id)
    }

    // ── Utilitaire : calcul début/fin de mois ────────────

    fun getMonthRange(calendar: Calendar): Pair<Long, Long> {
        val cal = calendar.clone() as Calendar

        // Début du mois : 1er jour à 00:00:00
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        // Fin du mois : dernier jour à 23:59:59
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    // ── Nom du mois affiché (ex: "Mars 2026") ────────────

    fun getMonthLabel(calendar: Calendar): String {
        val months = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )
        val month = months[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)
        return "$month $year"
    }
}