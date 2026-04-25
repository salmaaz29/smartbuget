package ma.fstt.smartbuget.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.fstt.smartbuget.R
import ma.fstt.smartbuget.viewmodel.CategoryViewModel
import ma.fstt.smartbuget.viewmodel.StatsViewModel

class StatsFragment : Fragment() {

    private val statsViewModel: StatsViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    private lateinit var adapter: StatsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvMonth: TextView
    private lateinit var layoutEmpty: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewStats)
        tvTotal = view.findViewById(R.id.tvTotalMonth)
        tvMonth = view.findViewById(R.id.tvCurrentMonth)
        layoutEmpty = view.findViewById(R.id.layoutEmptyStats)

        adapter = StatsAdapter(emptyList(), emptyList(), 0.0)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Navigation mois
        view.findViewById<ImageButton>(R.id.btnPreviousMonth).setOnClickListener {
            statsViewModel.goToPreviousMonth()
        }
        view.findViewById<ImageButton>(R.id.btnNextMonth).setOnClickListener {
            statsViewModel.goToNextMonth()
        }

        // Observer mois
        statsViewModel.currentCalendar.observe(viewLifecycleOwner) { cal ->
            tvMonth.text = statsViewModel.getMonthLabel(cal)
        }

        // Observer total
        statsViewModel.monthTotal.observe(viewLifecycleOwner) { total ->
            tvTotal.text = String.format("%.2f MAD", total ?: 0.0)
        }

        // Observer stats par catégorie
        statsViewModel.categoryTotals.observe(viewLifecycleOwner) { totals ->
            val categories = categoryViewModel.allCategories.value ?: emptyList()
            val total = statsViewModel.monthTotal.value ?: 0.0
            if (totals.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                layoutEmpty.visibility = View.GONE
                adapter.updateData(totals, categories, total)
            }
        }

        // Observer catégories
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val totals = statsViewModel.categoryTotals.value ?: emptyList()
            val total = statsViewModel.monthTotal.value ?: 0.0
            if (totals.isNotEmpty()) {
                adapter.updateData(totals, categories, total)
            }
        }
    }
}