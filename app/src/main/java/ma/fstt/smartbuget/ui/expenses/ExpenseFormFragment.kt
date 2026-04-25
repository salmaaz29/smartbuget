package ma.fstt.smartbuget.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.entity.Expense
import ma.fstt.smartbuget.viewmodel.CategoryViewModel
import ma.fstt.smartbuget.viewmodel.ExpenseViewModel
import java.util.Calendar

class ExpenseFormFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    private var expenseId: Int = -1
    private var currentExpense: Expense? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expense_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        expenseId = arguments?.getInt("expenseId", -1) ?: -1

        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategoryForm)
        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val etNote = view.findViewById<TextInputEditText>(R.id.etNote)
        val spinnerPayment = view.findViewById<Spinner>(R.id.spinnerPaymentMethod)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Setup du Spinner paiement (modes de paiement)
        val paymentMethods = listOf("Espèce", "Carte bancaire", "Virement")
        val paymentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, paymentMethods)
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPayment.adapter = paymentAdapter

        // === Chargement de la dépense si on est en mode modification ===
        if (expenseId != -1) {
            lifecycleScope.launch {
                val expense = expenseViewModel.getExpenseById(expenseId)
                if (expense != null) {
                    currentExpense = expense
                    etAmount.setText(expense.amount.toString())
                    etNote.setText(expense.note ?: "")

                    // On mettra la date après
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    etDate.setText(sdf.format(java.util.Date(expense.date)))
                }
            }
        }

        // === Observer les catégories ===
        categoryViewModel.activeCategories.observe(viewLifecycleOwner) { categories ->
            val items = categories.map { "${it.icon} ${it.name}" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter

            // Si modification → sélectionner la catégorie correspondante
            currentExpense?.let { exp ->
                val index = categories.indexOfFirst { it.id == exp.categoryId }
                if (index >= 0) spinnerCategory.setSelection(index)
            }
        }

        // === DatePicker ===
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                val dateStr = String.format("%02d/%02d/%04d", day, month + 1, year)
                etDate.setText(dateStr)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // === Bouton Enregistrer ===
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString().trim()

            if (amountText.isEmpty() || amountText.toDoubleOrNull() ?: 0.0 <= 0) {
                etAmount.error = "Montant doit être supérieur à 0"
                return@setOnClickListener
            }

            val selectedIndex = spinnerCategory.selectedItemPosition
            if (selectedIndex < 0) {
                Toast.makeText(requireContext(), "Veuillez choisir une catégorie", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categories = categoryViewModel.activeCategories.value ?: return@setOnClickListener
            val selectedCategory = categories[selectedIndex]

            val dateStr = etDate.text.toString().trim()
            if (dateStr.isEmpty()) {
                etDate.error = "La date est obligatoire"
                return@setOnClickListener
            }

            // Conversion date String → timestamp
            try {
                val dateParts = dateStr.split("/")
                val date = Calendar.getInstance().apply {
                    set(dateParts[2].toInt(), dateParts[1].toInt() - 1, dateParts[0].toInt(), 0, 0, 0)
                }.timeInMillis

                val note = etNote.text.toString().trim().ifEmpty { null }
                val paymentMethod = spinnerPayment.selectedItem?.toString() ?: "Espèce"

                val expense = Expense(
                    id = if (expenseId == -1) 0 else expenseId,
                    amount = amountText.toDouble(),
                    date = date,
                    categoryId = selectedCategory.id,
                    note = note,
                    paymentMethod = paymentMethod,
                    currency = "MAD"
                )

                if (expenseId == -1) {
                    expenseViewModel.addExpense(expense)
                } else {
                    expenseViewModel.updateExpense(expense)
                }

                Toast.makeText(requireContext(), "Dépense enregistrée avec succès ✓", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur dans la date", Toast.LENGTH_SHORT).show()
            }
        }
    }
}