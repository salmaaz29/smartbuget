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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.data.entity.Expense
import ma.fstt.smartbuget.viewmodel.CategoryViewModel
import ma.fstt.smartbuget.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFormFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    private var expenseId: Int = -1
    private var currentExpense: Expense? = null
    private var selectedDateMillis: Long = System.currentTimeMillis()

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

        // Références vues
        val tvTitle = view.findViewById<TextView>(R.id.tvFormTitle)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategoryForm)
        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val etNote = view.findViewById<TextInputEditText>(R.id.etNote)
        val spinnerPayment = view.findViewById<Spinner>(R.id.spinnerPaymentMethod)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        // Titre selon mode
        tvTitle.text = if (expenseId == -1) "Nouvelle dépense" else "Modifier dépense"

        // Date par défaut = aujourd'hui
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etDate.setText(sdf.format(Date(selectedDateMillis)))

        // Setup paiement
        val paymentMethods = listOf("Espèce", "Carte bancaire", "Virement")
        spinnerPayment.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            paymentMethods
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Charger dépense existante si modification
        if (expenseId != -1) {
            lifecycleScope.launch {
                val expense = expenseViewModel.getExpenseById(expenseId)
                if (expense != null) {
                    currentExpense = expense
                    selectedDateMillis = expense.date
                    etAmount.setText(expense.amount.toString())
                    etNote.setText(expense.note ?: "")
                    etDate.setText(sdf.format(Date(expense.date)))

                    // Mode de paiement
                    val payIndex = paymentMethods.indexOf(expense.paymentMethod)
                    if (payIndex >= 0) spinnerPayment.setSelection(payIndex)
                }
            }
        }

        // Observer catégories
        categoryViewModel.activeCategories.observe(viewLifecycleOwner) { categories ->
            val items = categories.map { "${it.icon} ${it.name}" }
            spinnerCategory.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            // Sélectionner catégorie en mode modification
            currentExpense?.let { exp ->
                val index = categories.indexOfFirst { it.id == exp.categoryId }
                if (index >= 0) spinnerCategory.setSelection(index)
            }
        }

        // DatePicker
        etDate.setOnClickListener { showDatePicker(etDate) }

        // Bouton Annuler → retour
        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        // Bouton retour flèche → retour
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Bouton Enregistrer
        btnSave.setOnClickListener {
            saveExpense(etAmount, spinnerCategory, etDate, etNote, spinnerPayment)
        }
    }

    private fun showDatePicker(etDate: TextInputEditText) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDateMillis

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, day, 12, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedDateMillis = selected.timeInMillis
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etDate.setText(sdf.format(Date(selectedDateMillis)))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveExpense(
        etAmount: TextInputEditText,
        spinnerCategory: Spinner,
        etDate: TextInputEditText,
        etNote: TextInputEditText,
        spinnerPayment: Spinner
    ) {
        // Validation montant
        val amountText = etAmount.text.toString().trim()
        if (amountText.isEmpty() || (amountText.toDoubleOrNull() ?: 0.0) <= 0.0) {
            etAmount.error = "Montant doit être supérieur à 0"
            return
        }

        // Validation catégorie
        val categories = categoryViewModel.activeCategories.value
        if (categories.isNullOrEmpty() || spinnerCategory.selectedItemPosition < 0) {
            Toast.makeText(requireContext(), "Veuillez choisir une catégorie", Toast.LENGTH_SHORT).show()
            return
        }

        // Validation date
        if (etDate.text.toString().trim().isEmpty()) {
            etDate.error = "La date est obligatoire"
            return
        }

        val selectedCategory = categories[spinnerCategory.selectedItemPosition]
        val note = etNote.text.toString().trim().ifEmpty { null }
        val paymentMethod = spinnerPayment.selectedItem?.toString() ?: "Espèce"

        val expense = Expense(
            id = if (expenseId == -1) 0 else expenseId,
            amount = amountText.toDouble(),
            date = selectedDateMillis,
            categoryId = selectedCategory.id,
            note = note,
            paymentMethod = paymentMethod,
            currency = "MAD"
        )

        if (expenseId == -1) {
            expenseViewModel.addExpense(expense)
            Toast.makeText(requireContext(), "✅ Dépense ajoutée !", Toast.LENGTH_SHORT).show()
        } else {
            expenseViewModel.updateExpense(expense)
            Toast.makeText(requireContext(), "✅ Dépense modifiée !", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }
}