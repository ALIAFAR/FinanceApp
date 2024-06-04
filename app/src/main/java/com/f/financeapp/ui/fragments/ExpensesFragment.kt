package com.f.financeapp.ui.fragments


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.f.financeapp.R
import com.f.financeapp.entities.Expense
import com.f.financeapp.entities.Income
import com.f.financeapp.ui.activities.AddChangeExpenseActivity
import com.f.financeapp.ui.activities.ExpensesActivity
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.f.financeapp.ui.viewmodels.ExpensesViewModel
import com.f.financeapp.ui.viewmodels.IncomeViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExpensesFragment : Fragment() {

    lateinit var vmExpenses: ExpensesViewModel
    lateinit var vmIncome: IncomeViewModel // Добавлено
    lateinit var vmCategories: CategoryViewModel
    private lateinit var pieChartExpenses: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnShowAllExpenses = view.findViewById<Button>(R.id.btnShowAllExpenses)
        btnShowAllExpenses.setOnClickListener {
            val intent = Intent(view.context, ExpensesActivity::class.java)
            startActivity(intent)
        }

        val btnAddExpenses = view.findViewById<Button>(R.id.btnAddExpense)
        btnAddExpenses.setOnClickListener {
            val intent = Intent(view.context, AddChangeExpenseActivity::class.java)
            startActivity(intent)
        }

        val tvExpensesByDay = view.findViewById<TextView>(R.id.tvExpensesByDay)
        val tvExpensesByMonth = view.findViewById<TextView>(R.id.tvExpensesByMonth)
        val tvExpensesByYear = view.findViewById<TextView>(R.id.tvExpensesByYear)
        val tvBalance = view.findViewById<TextView>(R.id.tvBalance) // Новый TextView для баланса
        pieChartExpenses = view.findViewById(R.id.pieChartExpenses)

        vmCategories = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(CategoryViewModel::class.java)

        vmExpenses = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(ExpensesViewModel::class.java)

        vmIncome = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(IncomeViewModel::class.java) // Инициализация IncomeViewModel

        val btnPreviousMonth = view.findViewById<Button>(R.id.btnPreviousMonth)

        // Обзор расходов и доходов в фрагменте
        val expensesObserver = Observer<List<Expense>> { expenseList ->
            val incomeObserver = Observer<List<Income>> { incomeList ->
                // Вычисление сумм расходов
                val expensesByDay = expenseList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE) == LocalDate.now()
                }.sumOf { it.sum }
                val expensesByMonthList = expenseList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(
                        LocalDate.now().withDayOfMonth(1).plusDays(-1)
                    )
                }
                val expensesByYear = expenseList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(
                        LocalDate.now().withDayOfMonth(1).plusYears(-1).plusDays(-1)
                    )
                }.sumOf { it.sum }

                val totalExpenses = expenseList.sumOf { it.sum }

                // Вычисление сумм доходов
                val totalIncome = incomeList.sumOf { it.sum }

                // Вычисление баланса
                val balance = totalIncome - totalExpenses

                val decimalFormat = DecimalFormat("0.#")

                tvExpensesByDay.text =
                    "Расходы за день: " + decimalFormat.format(expensesByDay) + " руб."
                tvExpensesByMonth.text =
                    "Расходы за месяц: " + decimalFormat.format(expensesByMonthList.sumOf { it.sum }) + " руб."
                tvExpensesByYear.text =
                    "Расходы за год: " + decimalFormat.format(expensesByYear) + " руб."
                tvBalance.text =
                    "Баланс: " + decimalFormat.format(balance) + " руб."

                // Вывод списка расходов за последний месяц
                showPieChartForMonth(expensesByMonthList)

                // Получение списка расходов за предпоследний месяц
                var expensesByPreviousMonthList = expenseList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(
                        LocalDate.now().withDayOfMonth(1).plusMonths(-1).plusDays(-1)
                    )
                }
                expensesByPreviousMonthList = expensesByPreviousMonthList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isBefore(
                        LocalDate.now().withDayOfMonth(1).plusDays(-1)
                    )
                }

                btnPreviousMonth.setOnClickListener {
                    showPieChartForMonth(expensesByPreviousMonthList)
                }
            }

            vmIncome.allIncome.observe(viewLifecycleOwner, incomeObserver)
        }

        vmExpenses.allExpenses.observe(viewLifecycleOwner, expensesObserver)
    }


    fun showPieChartForMonth(expensesByMonthList: List<Expense>) {
        val pieEntry = ArrayList<PieEntry>()

        val distinctCategoriesIds = expensesByMonthList.flatMap { listOf(it.category) }.distinct()

        // Формирование списка цветов для вывода графика
        val colorArray = context?.resources?.getIntArray(R.array.pie_chart_color_array)
        val colors: ArrayList<Int> = ArrayList()
        if (colorArray != null) {
            for (color in colorArray) {
                colors.add(color)
            }
        }
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        pieChartExpenses.isDrawHoleEnabled = true
        pieChartExpenses.description.isEnabled = false
        pieChartExpenses.legend.horizontalAlignment =
            Legend.LegendHorizontalAlignment.CENTER
        pieChartExpenses.isDrawHoleEnabled = true
        pieChartExpenses.description.isEnabled = false
        pieChartExpenses.legend.isEnabled = true
        pieChartExpenses.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieChartExpenses.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChartExpenses.legend.isWordWrapEnabled = true
        pieChartExpenses.animateY(1200, Easing.EaseInOutQuad)
        pieChartExpenses.setDrawEntryLabels(false)

        val dataSet = PieDataSet(pieEntry, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 2f
        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(18f)

        // Формирование списка сумм расходов, связанных с категорией
        for (categoryId in distinctCategoriesIds) {
            val expensesByCategorySum =
                expensesByMonthList.filter { it.category == categoryId }
                    .flatMap { listOf(it.sum) }.sum()
            vmCategories.getCategoryById(categoryId).observe(viewLifecycleOwner) {
                if (it != null) {
                    pieEntry.add(PieEntry(expensesByCategorySum.toFloat(), it.name))
                }
                pieChartExpenses.data = data
            }
        }
        pieChartExpenses.invalidate()
    }
}
