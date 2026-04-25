package ma.fstt.smartbuget.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ma.fstt.smartbuget.data.dao.CategoryTotal
import ma.fstt.smartbuget.data.database.AppDatabase
import ma.fstt.smartbuget.data.repository.ExpenseRepository
import java.util.Calendar

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository by lazy {
        val dao = AppDatabase.getDatabase(application).expenseDao()
        ExpenseRepository(dao)
    }

    private val _currentCalendar = MutableLiveData(Calendar.getInstance())
    val currentCalendar: LiveData<Calendar> = _currentCalendar

    val categoryTotals: LiveData<List<CategoryTotal>> = _currentCalendar.switchMap { cal ->
        val (start, end) = getMonthRange(cal)
        repository.getTotalByCategoryForMonth(start, end)
    }

    val monthTotal: LiveData<Double> = _currentCalendar.switchMap { cal ->
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