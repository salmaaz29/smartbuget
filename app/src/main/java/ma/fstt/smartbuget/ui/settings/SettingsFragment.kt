package ma.fstt.smartbuget.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.entity.Category
import ma.fstt.smartbuget.viewmodel.CategoryViewModel

class SettingsFragment : Fragment() {

    private val categoryViewModel: CategoryViewModel by activityViewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCategories)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddCategory)

        // Setup adapter
        adapter = CategoryAdapter(
            categories = emptyList(),
            onToggle = { category ->
                categoryViewModel.toggleCategoryActive(category)
            },
            onDelete = { category ->
                confirmDeleteCategory(category)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observer toutes les catégories
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
        }

        // Observer erreurs
        categoryViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                AlertDialog.Builder(requireContext())
                    .setTitle("Erreur")
                    .setMessage(it)
                    .setPositiveButton("OK") { _, _ ->
                        categoryViewModel.clearError()
                    }
                    .show()
            }
        }

        // Bouton ajouter catégorie
        btnAdd.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_category, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Nouvelle catégorie")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { _, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etCategoryName)
                    .text.toString().trim()
                val icon = dialogView.findViewById<EditText>(R.id.etCategoryIcon)
                    .text.toString().trim()

                if (name.isNotEmpty()) {
                    categoryViewModel.addCategory(
                        Category(
                            name = name,
                            icon = icon.ifEmpty { "📦" },
                            color = "#607D8B"
                        )
                    )
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun confirmDeleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Supprimer")
            .setMessage("Supprimer la catégorie \"${category.name}\" ?")
            .setPositiveButton("Supprimer") { _, _ ->
                categoryViewModel.deleteCategory(category)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}