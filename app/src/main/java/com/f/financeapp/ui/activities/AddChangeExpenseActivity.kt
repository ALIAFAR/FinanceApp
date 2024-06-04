package com.f.financeapp.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.R
import com.f.financeapp.adapters.CategoriesRVAdapter
import com.f.financeapp.adapters.CategoryClickInterface
import com.f.financeapp.adapters.CategoryLongClickInterface
import com.f.financeapp.entities.Category
import com.f.financeapp.entities.Expense
import com.f.financeapp.entities.Income
import com.f.financeapp.notifications.NotificationSender
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.f.financeapp.ui.viewmodels.ExpensesViewModel
import com.f.financeapp.ui.viewmodels.IncomeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddChangeExpenseActivity : AppCompatActivity(), CategoryClickInterface, CategoryLongClickInterface {
    lateinit var etSum: EditText
    lateinit var etTitle: EditText
    lateinit var etComment: EditText
    lateinit var cvDate: CalendarView
    lateinit var btnSave: Button
    var balance: Double = 0.0

    lateinit var vmExpense: ExpensesViewModel
    lateinit var vmIncome: IncomeViewModel
    var expenseId = -1

    lateinit var chosenDate: String

    lateinit var vmCategories: CategoryViewModel
    lateinit var rvCategories: RecyclerView

    var chosenCategoryId = -1

    lateinit var ivCategoryIcon: ImageView
    lateinit var tvCategoryName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_change_expense)

        vmCategories = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(CategoryViewModel::class.java)
        vmExpense = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(ExpensesViewModel::class.java)
        vmIncome = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(IncomeViewModel::class.java)

        etSum = findViewById(R.id.etSum)
        etTitle = findViewById(R.id.etExpenseTitle)
        etComment = findViewById(R.id.etComment)
        cvDate = findViewById(R.id.cvDate)
        btnSave = findViewById(R.id.btnSave)
        ivCategoryIcon = findViewById(R.id.ivCategoryIcon)
        tvCategoryName = findViewById(R.id.tvCategoryName)

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val action = intent.getStringExtra("action")
        if (action == "edit") {
            val sum = intent.getDoubleExtra("sum", 0.0)
            chosenCategoryId = intent.getIntExtra("category", -1)
            val date = intent.getStringExtra("date")
            val expenseTitle = intent.getStringExtra("title")
            val comment = intent.getStringExtra("comment")
            etSum.setText(sum.toString())
            etTitle.setText(expenseTitle)
            etComment.setText(comment)
            btnSave.text = "Обновить"
            expenseId = intent.getIntExtra("id", -1)
            title = "Изменение расхода"
            val cal = Calendar.getInstance()
            cal.time = sdf.parse(date)
            cvDate.date = cal.timeInMillis

            GlobalScope.launch {
                val category = vmCategories.repository.getCategoryById(chosenCategoryId)
                category?.let {
                    ivCategoryIcon.setImageResource(it.iconResource)
                    tvCategoryName.text = it.name
                }
            }
        } else {
            btnSave.text = "Сохранить"
            title = "Добавление расхода"
        }

        chosenDate = sdf.format(Date())

        cvDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val realMonth = month + 1
            chosenDate = if (realMonth < 10) {
                if (dayOfMonth < 10) {
                    "$year-0$realMonth-0$dayOfMonth"
                } else {
                    "$year-0$realMonth-$dayOfMonth"
                }
            } else {
                if (dayOfMonth < 10) {
                    "$year-$realMonth-0$dayOfMonth"
                } else {
                    "$year-$realMonth-$dayOfMonth"
                }
            }
        }

        val expensesObserver = Observer<List<Expense>> { expenseList ->
            val totalExpenses = expenseList.sumOf { it.sum }
            vmIncome.allIncome.observe(this, Observer { incomeList ->
                val totalIncome = incomeList.sumOf { it.sum }
                balance = totalIncome - totalExpenses
            })
        }

        vmExpense.allExpenses.observe(this, expensesObserver)

        btnSave.setOnClickListener {
            try {
                if (chosenCategoryId == -1) {
                    Toast.makeText(this, "Не выбрана категория!", Toast.LENGTH_SHORT).show()
                    throw IllegalArgumentException("Не выбрана категория!")
                }
                if (etSum.text.toString().isEmpty()) {
                    Toast.makeText(this, "Не указана сумма!", Toast.LENGTH_SHORT).show()
                    throw IllegalArgumentException("Не указана сумма!")
                }

                val sum = etSum.text.toString().toDouble()
                val date = chosenDate
                val title = etTitle.text.toString()
                val comment = etComment.text.toString()
                

                if (sum > balance) {
                    Toast.makeText(this, "Расход превышает баланс!", Toast.LENGTH_SHORT).show()
                    throw IllegalArgumentException("Расход превышает баланс!")
                }

                if (action == "edit") {
                    val updatedExpense = Expense(sum, chosenCategoryId, date, title, comment).apply { id = expenseId }
                    vmExpense.updateExpense(updatedExpense)
                    Toast.makeText(this, "Данные о расходах обновлены", Toast.LENGTH_LONG).show()
                } else {
                    vmExpense.addExpense(Expense(sum, chosenCategoryId, date, title, comment))
                    Toast.makeText(this, "Данные о $title добавлены", Toast.LENGTH_LONG).show()
                }
                startActivity(Intent(applicationContext, FinanceActivity::class.java))
                checkExpensesLimit()
            } catch (e: IllegalArgumentException) {
                Log.e("AddChangeExpenseActivity", "Error: ${e.message}")
            } catch (e: Exception) {
                Log.e("AddChangeExpenseActivity", "Unexpected error: ${e.message}")
            }
        }

        rvCategories = findViewById(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this)
        val categoriesRVAdapter = CategoriesRVAdapter(this, this, this)
        rvCategories.adapter = categoriesRVAdapter

        vmCategories.allCategories.observe(this, Observer { list ->
            list?.let { categoriesRVAdapter.updateList(it) }
        })
    }

    override fun onCategoryClick(category: Category) {
        chosenCategoryId = category.id
        ivCategoryIcon.setImageResource(category.iconResource)
        tvCategoryName.text = category.name
    }

    override fun onCategoryLongClick(category: Category) {}

    private fun checkExpensesLimit() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val limit = prefs.getString("limit", "")?.toDouble()

        limit?.let {
            vmExpense.allExpenses.observe(this, Observer { list ->
                val expensesByMonthSum = list.filter {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE).isAfter(LocalDate.now().minusMonths(1))
                }.sumOf { it.sum }

                if (expensesByMonthSum > it) {
                    NotificationSender.sendLimitNotification(application, it.toString(), expensesByMonthSum - it)
                }
            })
        }
    }
}
