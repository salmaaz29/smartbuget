package ma.fstt.smartbuget.ui.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.entity.Category
import ma.fstt.smartbuget.data.entity.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private var categories: List<Category>,
    private val onEdit: (Expense) -> Unit,
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        val category = categories.find { it.id == expense.categoryId }

        // Icône et nom catégorie
        holder.tvIcon.text = category?.icon ?: "📦"
        holder.tvCategory.text = category?.name ?: "Autre"

        // Note
        if (expense.note.isNullOrEmpty()) {
            holder.tvNote.visibility = View.GONE
        } else {
            holder.tvNote.visibility = View.VISIBLE
            holder.tvNote.text = expense.note
        }

        // Montant
        holder.tvAmount.text = String.format("%.2f %s", expense.amount, expense.currency)

        // Date
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(expense.date))

        // Clic long → supprimer
        holder.itemView.setOnLongClickListener {
            onDelete(expense)
            true
        }

        // Clic simple → modifier
        holder.itemView.setOnClickListener {
            onEdit(expense)
        }
    }

    override fun getItemCount() = expenses.size

    // Mettre à jour la liste
    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}