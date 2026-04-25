package ma.fstt.smartbuget.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.entity.Category
import ma.fstt.smartbuget.data.entity.Expense
import ma.fstt.smartbuget.viewmodel.CategoryViewModel
import ma.fstt.smartbuget.viewmodel.ExpenseViewModel

class ExpensesFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    private lateinit var adapter: ExpenseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvMonth: TextView
    private lateinit var layoutEmpty: View
    private lateinit var spinnerCategory: Spinner

    private var categoriesList = listOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Références vues
        recyclerView = view.findViewById(R.id.recyclerViewExpenses)
        tvTotal = view.findViewById(R.id.tvTotalMonth)
        tvMonth = view.findViewById(R.id.tvCurrentMonth)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)

        // Setup RecyclerView
        adapter = ExpenseAdapter(
            expenses = emptyList(),
            categories = emptyList(),
            onEdit = { expense -> navigateToForm(expense) },
            onDelete = { expense -> confirmDelete(expense) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Navigation mois
        view.findViewById<ImageButton>(R.id.btnPreviousMonth).setOnClickListener {
            expenseViewModel.goToPreviousMonth()
        }
        view.findViewById<ImageButton>(R.id.btnNextMonth).setOnClickListener {
            expenseViewModel.goToNextMonth()
        }

        // Bouton ajouter
        view.findViewById<FloatingActionButton>(R.id.fabAddExpense).setOnClickListener {
            navigateToForm(null)
        }

        // CORRECTION : Observer mois courant (on ne fait qu'afficher le label du mois)
        expenseViewModel.currentCalendar.observe(viewLifecycleOwner) { cal ->
            tvMonth.text = expenseViewModel.getMonthLabel(cal)
        }

        // Observer dépenses
        expenseViewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            adapter.updateExpenses(expenses)
            if (expenses.isEmpty()) {
                recyclerView.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                layoutEmpty.visibility = View.GONE
            }
        }

        // Observer total
        expenseViewModel.totalByMonth.observe(viewLifecycleOwner) { total ->
            // CORRECTION : On gère le cas où le total est null (0.00 MAD)
            tvTotal.text = String.format("%.2f MAD", total ?: 0.0)
        }

        // Observer catégories
        categoryViewModel.activeCategories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            adapter.updateCategories(categories)
            setupCategorySpinner(categories)
        }
    }

    private fun setupCategorySpinner(categories: List<Category>) {
        val items = mutableListOf("Toutes les catégories")
        items.addAll(categories.map { "${it.icon} ${it.name}" })

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == 0) {
                    expenseViewModel.selectCategory(null)
                } else {
                    expenseViewModel.selectCategory(categoriesList[pos - 1].id)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun navigateToForm(expense: Expense?) {
        val bundle = Bundle()
        bundle.putInt("expenseId", expense?.id ?: -1)
        findNavController().navigate(R.id.expenseFormFragment, bundle)
    }

    private fun confirmDelete(expense: Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("Supprimer")
            .setMessage("Voulez-vous supprimer cette dépense ?")
            .setPositiveButton("Supprimer") { _, _ ->
                expenseViewModel.deleteExpense(expense)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}