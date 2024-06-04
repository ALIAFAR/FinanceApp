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
import com.f.financeapp.entities.Income
import com.f.financeapp.entities.Expense
import com.f.financeapp.ui.activities.AddChangeIncomeActivity
import com.f.financeapp.ui.activities.IncomeActivity
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.f.financeapp.ui.viewmodels.IncomeViewModel
import com.f.financeapp.ui.viewmodels.ExpensesViewModel
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

class IncomeFragment : Fragment() {

    lateinit var vmIncome: IncomeViewModel
    lateinit var vmExpenses: ExpensesViewModel
    lateinit var vmCategories: CategoryViewModel
    private lateinit var pieChartIncome: PieChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_income, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnShowAllIncome = view.findViewById<Button>(R.id.btnShowAllIncome)

        btnShowAllIncome.setOnClickListener {
            val intent = Intent(view.context, IncomeActivity::class.java)
            startActivity(intent)
        }

        val btnAddIncome = view.findViewById<Button>(R.id.btnAddIncome)
        btnAddIncome.setOnClickListener {
            val intent = Intent(view.context, AddChangeIncomeActivity::class.java)
            startActivity(intent)
        }

        val tvIncomeByDay = view.findViewById<TextView>(R.id.tvIncomeByDay)
        val tvIncomeByMonth = view.findViewById<TextView>(R.id.tvIncomeByMonth)
        val tvIncomeByYear = view.findViewById<TextView>(R.id.tvIncomeByYear)
        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
        pieChartIncome = view.findViewById(R.id.pieChartIncome)

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
        ).get(IncomeViewModel::class.java)


        val btnPreviousMonth = view.findViewById<Button>(R.id.btnPreviousMonth)

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
                val incomeByDay = incomeList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE) == LocalDate.now()
                }.sumOf { it.sum }
                val incomeByMonthList = incomeList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(
                        LocalDate.now().withDayOfMonth(1).plusDays(-1)
                    )
                }
                val incomeByYear = incomeList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(
                        LocalDate.now().withDayOfMonth(1).plusYears(-1).plusDays(-1)
                    )
                }.sumOf { it.sum }

                val totalIncome = incomeList.sumOf { it.sum }

                // Вычисление баланса
                val balance = totalIncome - totalExpenses

                val decimalFormat = DecimalFormat("0.#")

                tvIncomeByDay.text =
                    "Доходы за день: " + decimalFormat.format(incomeByDay) + " руб."
                tvIncomeByMonth.text =
                    "Доходы за месяц: " + decimalFormat.format(incomeByMonthList.sumOf { it.sum }) + " руб."
                tvIncomeByYear.text =
                    "Доходы за год: " + decimalFormat.format(incomeByYear) + " руб."
                tvBalance.text =
                    "Баланс: " + decimalFormat.format(balance) + " руб."

                // Вывод списка расходов за последний месяц
                showPieChartForMonth(incomeByMonthList)

                // Получение списка расходов за предпоследний месяц
                var incomeByPreviousMonthList = incomeList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(
                        LocalDate.now().withDayOfMonth(1).plusMonths(-1).plusDays(-1)
                    )
                }
                incomeByPreviousMonthList = incomeByPreviousMonthList.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isBefore(
                        LocalDate.now().withDayOfMonth(1).plusDays(-1)
                    )
                }

                btnPreviousMonth.setOnClickListener {
                    showPieChartForMonth(incomeByPreviousMonthList)
                }
            }

            vmIncome.allIncome.observe(viewLifecycleOwner, incomeObserver)
        }

        vmExpenses.allExpenses.observe(viewLifecycleOwner, expensesObserver)
    }

    fun showPieChartForMonth(incomeByMonthList: List<Income>){
        val pieEntry = ArrayList<PieEntry>()

//        for (income in incomeByMonthList){
//            pieEntry.add(PieEntry(income.sum.toFloat(), income.title))
//        }

        val distinctCategoriesIds = incomeByMonthList.flatMap { listOf(it.category) }.distinct()

        // формирования списка цветов для вывода графика
        val colorArray = context?.resources?.getIntArray(R.array.pie_chart_color_array2)
        val colors: ArrayList<Int> = ArrayList()
        if (colorArray != null) {
            for (color in colorArray) {
                colors.add(color)
            }
        }
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        pieChartIncome.animateY(800, Easing.EaseInOutQuad)
        pieChartIncome.isDrawHoleEnabled = false
        pieChartIncome.description.isEnabled = false
        pieChartIncome.legend.isEnabled = true
        pieChartIncome.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        pieChartIncome.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieChartIncome.legend.isWordWrapEnabled = true
        pieChartIncome.setDrawEntryLabels(false)
        pieChartIncome.description.isEnabled = false
        pieChartIncome.legend.horizontalAlignment =
            Legend.LegendHorizontalAlignment.CENTER

        val dataSet  = PieDataSet(pieEntry, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 5f
        val data = PieData(dataSet)
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(18f)

        for (categoryId in distinctCategoriesIds){
            val expensesByCategorySum = incomeByMonthList.filter { it.category == categoryId }.flatMap { listOf(it.sum) }.sum()
            vmCategories.getCategoryById(categoryId).observe(viewLifecycleOwner){
                if (it != null) {
                    pieEntry.add(PieEntry(expensesByCategorySum.toFloat(), it.name))
                }
                pieChartIncome.data = data
            }
        }

        pieChartIncome.invalidate()
    }
}