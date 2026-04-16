package ma.fstt.smartbuget.data.repository

import androidx.lifecycle.LiveData
import ma.fstt.smartbuget.data.dao.CategoryTotal
import ma.fstt.smartbuget.data.dao.ExpenseDao
import ma.fstt.smartbuget.data.entity.Expense


    class ExpenseRepository(private val expenseDao: ExpenseDao) {

        fun getExpensesByMonth(start: Long, end: Long): LiveData<List<Expense>> =
            expenseDao.getExpensesByMonth(start, end)

        fun getExpensesByMonthAndCategory(
            start: Long,
            end: Long,
            categoryId: Int
        ): LiveData<List<Expense>> =
            expenseDao.getExpensesByMonthAndCategory(start, end, categoryId)

        fun getTotalByMonth(start: Long, end: Long): LiveData<Double> =
            expenseDao.getTotalByMonth(start, end)

        fun getTotalByCategoryForMonth(start: Long, end: Long): LiveData<List<CategoryTotal>> =
            expenseDao.getTotalByCategoryForMonth(start, end)

        suspend fun insert(expense: Expense) = expenseDao.insertExpense(expense)

        suspend fun update(expense: Expense) {
            val updated = expense.copy(updatedAt = System.currentTimeMillis())
            expenseDao.updateExpense(updated)
        }

        suspend fun delete(expense: Expense) = expenseDao.deleteExpense(expense)

        suspend fun getById(id: Int): Expense? = expenseDao.getExpenseById(id)

        suspend fun getForExport(start: Long, end: Long): List<Expense> =
            expenseDao.getExpensesForExport(start, end)


    }