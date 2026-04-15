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

    // toutes les depenses d un mois
    @Query("""SELECT * FROM expenses
            WHERE date >= :startofmonth AND date <= :endofmonth
            ORDER BY date DESC
    """)
    fun getExpensesByMonth(startofmonth: Long, endofmonth: Long): LiveData<List<Expense>>

    // Dépenses d'un mois filtrées par catégorie
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

    // Total des dépenses d'un mois
    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
    """)
    fun getTotalByMonth(startOfMonth: Long, endOfMonth: Long): LiveData<Double>

    // Total par catégorie pour un mois (pour les stats)
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

    // Ajouter une dépense
    @Insert
    suspend fun insertExpense(expense: Expense): Long

    // Modifier une dépense
    @Update
    suspend fun updateExpense(expense: Expense)

    // Supprimer une dépense
    @Delete
    suspend fun deleteExpense(expense: Expense)

    // Récupérer une dépense par ID (pour modification)
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?

    // Toutes les dépenses d'un mois (sans LiveData, pour export CSV)
    @Query("""
        SELECT * FROM expenses 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        ORDER BY date DESC
    """)
    suspend fun getExpensesForExport(startOfMonth: Long, endOfMonth: Long): List<Expense>
}
// Classe helper pour total par catégorie
data class CategoryTotal(
    val categoryId: Int,
    val total: Double
)