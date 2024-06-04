package com.f.financeapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.ui.activities.AddChangeExpenseActivity
import com.f.financeapp.R
import com.f.financeapp.adapters.ExpenseClickInterface
import com.f.financeapp.adapters.ExpenseLongClickInterface
import com.f.financeapp.adapters.ExpenseRVAdapter
import com.f.financeapp.entities.Expense
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.f.financeapp.ui.viewmodels.ExpensesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

// активити для вывода списка расходов
// реализует интерфейс ExpenseClickInterface для обработки нажатий на элементы списка RecyclerView с расходами
// реализует интерфейс ExpenseLongClickInterface для обработки длинных нажатий на элементы списка RecyclerView с расходами
class ExpensesActivity : AppCompatActivity(), ExpenseClickInterface, ExpenseLongClickInterface {
    lateinit var vmExpenses: ExpensesViewModel

    lateinit var rvExpenses: RecyclerView
    lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)
        title = "Расходы"

        rvExpenses = findViewById(R.id.rvExpenses)
        fabAdd = findViewById(R.id.fabAddExpense)

        vmExpenses = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ExpensesViewModel::class.java)

        rvExpenses.layoutManager = LinearLayoutManager(this)

        val expensesRVAdapter = ExpenseRVAdapter(this, this, this)

        rvExpenses.adapter = expensesRVAdapter

        val vmCategories: CategoryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CategoryViewModel::class.java)

        vmCategories.allCategories.observe(this, Observer { list ->
            list?.let {
                expensesRVAdapter.setCategories(it)
            }
        })

        vmExpenses.allExpenses.observe(this, Observer { list ->
            list?.let {
                expensesRVAdapter.updateList(it)
            }
        })

        // обработчик для кнопки добавления нового объекта
        fabAdd.setOnClickListener{
            val intent = Intent(this@ExpensesActivity, AddChangeExpenseActivity::class.java)
            startActivity(intent)
//            this.finish()
        }
    }

    // перегруженная функция для обработки нажатий на объекты RecyclerView
    override fun onExpenseClick(expense: Expense) {
        val intent = Intent(this@ExpensesActivity, AddChangeExpenseActivity::class.java)
        intent.putExtra("action", "edit")
        intent.putExtra("sum", expense.sum)
        intent.putExtra("category", expense.category)
        intent.putExtra("date", expense.date)
        intent.putExtra("title", expense.title)
        intent.putExtra("comment", expense.comment)
        intent.putExtra("id", expense.id)
        startActivity(intent)
//        this.finish()
    }

    // перегруженная функция для обработки длинных нажатий на объекты RecyclerView
    override fun onExpenseLongClick(expense: Expense) {
        vmExpenses.deleteExpense(expense)
        Toast.makeText(this, "Данные о расходах удалены", Toast.LENGTH_LONG).show()
    }
}