package ma.fstt.smartbuget.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.entity.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onToggle: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val switchActive: SwitchMaterial = itemView.findViewById(R.id.switchActive)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.tvIcon.text = category.icon
        holder.tvName.text = category.name

        // Désactiver le listener avant de changer l'état
        holder.switchActive.setOnCheckedChangeListener(null)
        holder.switchActive.isChecked = category.isActivate

        // Activer/désactiver catégorie
        holder.switchActive.setOnCheckedChangeListener { _, _ ->
            onToggle(category)
        }

        // Supprimer catégorie
        holder.btnDelete.setOnClickListener {
            onDelete(category)
        }
    }

    override fun getItemCount() = categories.size

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}