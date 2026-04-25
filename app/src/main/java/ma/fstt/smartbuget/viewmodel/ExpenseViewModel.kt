package ma.fstt.smartbuget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.data.database.AppDatabase
import ma.fstt.smartbuget.data.entity.Expense
import ma.fstt.smartbuget.data.repository.ExpenseRepository
import java.util.*

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository by lazy {
        val dao = AppDatabase.getDatabase(application).expenseDao()
        ExpenseRepository(dao)
    }

    private val _currentCalendar = MutableLiveData(Calendar.getInstance())
    val currentCalendar: LiveData<Calendar> = _currentCalendar

    private val _selectedCategoryId = MutableLiveData<Int?>(null)

    private val filterTrigger = MediatorLiveData<Pair<Calendar, Int?>>().apply {
        addSource(_currentCalendar) { cal ->
            value = Pair(cal, _selectedCategoryId.value)
        }
        addSource(_selectedCategoryId) { catId ->
            value = Pair(_currentCalendar.value ?: Calendar.getInstance(), catId)
        }
    }

    val expenses: LiveData<List<Expense>> = filterTrigger.switchMap { (cal, categoryId) ->
        val (start, end) = getMonthRange(cal)
        if (categoryId == null) {
            repository.getExpensesByMonth(start, end)
        } else {
            repository.getExpensesByMonthAndCategory(start, end, categoryId)
        }
    }

    val totalByMonth: LiveData<Double> = _currentCalendar.switchMap { cal ->
        val (start, end) = getMonthRange(cal)
        repository.getTotalByMonth(start, end).map { it ?: 0.0 }
    }

    fun goToPreviousMonth() {
        val cal = _currentCalendar.value?.clone() as? Calendar ?: return
        cal.add(Calendar.MONTH, -1)
        _currentCalendar.value = cal
    }

    fun goToNextMonth() {
        val cal = _currentCalendar.value?.clone() as? Calendar ?: return
        cal.add(Calendar.MONTH, 1)
        _currentCalendar.value = cal
    }

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    suspend fun getExpenseById(id: Int): Expense? = repository.getById(id)

    fun getMonthRange(calendar: Calendar): Pair<Long, Long> {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        return Pair(start, end)
    }

    fun getMonthLabel(calendar: Calendar): String {
        val months = listOf(
            "Janvier","Février","Mars","Avril","Mai","Juin",
            "Juillet","Août","Septembre","Octobre","Novembre","Décembre"
        )
        return "${months[calendar.get(Calendar.MONTH)]} ${calendar.get(Calendar.YEAR)}"
    }
}