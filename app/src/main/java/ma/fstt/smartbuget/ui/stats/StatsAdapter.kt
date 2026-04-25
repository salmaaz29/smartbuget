package ma.fstt.smartbuget.ui.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.dao.CategoryTotal
import ma.fstt.smartbuget.data.entity.Category

class StatsAdapter(
    private var categoryTotals: List<CategoryTotal>,
    private var categories: List<Category>,
    private var totalMonth: Double
) : RecyclerView.Adapter<StatsAdapter.StatViewHolder>() {

    inner class StatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tvStatIcon)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvStatCategoryName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvStatAmount)
        val tvPercentage: TextView = itemView.findViewById(R.id.tvStatPercentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stat, parent, false)
        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        val categoryTotal = categoryTotals[position]
        val category = categories.find { it.id == categoryTotal.categoryId } ?: return

        val percentage = if (totalMonth > 0)
            (categoryTotal.total / totalMonth * 100).toInt() else 0

        holder.tvIcon.text = category.icon
        holder.tvCategoryName.text = category.name
        holder.tvAmount.text = String.format("%.2f MAD", categoryTotal.total)
        holder.tvPercentage.text = "$percentage%"
        holder.progressBar.progress = percentage
    }

    override fun getItemCount() = categoryTotals.size

    fun updateData(
        newTotals: List<CategoryTotal>,
        newCategories: List<Category>,
        newTotal: Double
    ) {
        categoryTotals = newTotals
        categories = newCategories
        totalMonth = newTotal
        notifyDataSetChanged()
    }
}