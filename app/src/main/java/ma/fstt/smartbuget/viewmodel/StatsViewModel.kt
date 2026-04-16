package ma.fstt.smartbuget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ma.fstt.smartbuget.data.dao.CategoryTotal
import ma.fstt.smartbuget.data.database.AppDatabase
import ma.fstt.smartbuget.data.repository.ExpenseRepository
import java.util.Calendar

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    private val _currentCalendar = MutableLiveData(Calendar.getInstance())
    val currentCalendar: LiveData<Calendar> = _currentCalendar

    // Totaux par catégorie du mois
    val categoryTotals: LiveData<List<CategoryTotal>>

    // Total général du mois
    val monthTotal: LiveData<Double>

    init {
        val dao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)

        val (start, end) = getMonthRange(Calendar.getInstance())
        categoryTotals = repository.getTotalByCategoryForMonth(start, end)
        monthTotal = repository.getTotalByMonth(start, end)
    }

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

    private fun getMonthRange(calendar: Calendar): Pair<Long, Long> {
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