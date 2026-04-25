package ma.fstt.smartbuget.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ma.fstt.smartbuget.data.entity.Expense

@Dao
interface ExpenseDao {

    @Query("""
        SELECT * FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth 
        ORDER BY date DESC
    """)
    fun getExpensesByMonth(startOfMonth: Long, endOfMonth: Long): LiveData<List<Expense>>

    @Query("""
        SELECT * FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth 
        AND categoryId = :categoryId
        ORDER BY date DESC
    """)
    fun getExpensesByMonthAndCategory(
        startOfMonth: Long,
        endOfMonth: Long,
        categoryId: Int
    ): LiveData<List<Expense>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
    """)
    fun getTotalByMonth(startOfMonth: Long, endOfMonth: Long): LiveData<Double>

    @Query("""
        SELECT categoryId, SUM(amount) as total 
        FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        GROUP BY categoryId
        ORDER BY total DESC
    """)
    fun getTotalByCategoryForMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): LiveData<List<CategoryTotal>>

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?

    @Query("""
        SELECT * FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        ORDER BY date DESC
    """)
    suspend fun getExpensesForExport(startOfMonth: Long, endOfMonth: Long): List<Expense>
}

// Helper class - DOIT rester en dehors de l'interface
data class CategoryTotal(
    val categoryId: Int,
    val total: Double
)