package com.f.financeapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.R
import com.f.financeapp.adapters.IncomeClickInterface
import com.f.financeapp.adapters.IncomeLongClickInterface
import com.f.financeapp.adapters.IncomeRVAdapter
import com.f.financeapp.entities.Income
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.f.financeapp.ui.viewmodels.IncomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IncomeActivity : AppCompatActivity(), IncomeClickInterface, IncomeLongClickInterface {
    lateinit var vmIncome: IncomeViewModel
    lateinit var rvIncome: RecyclerView
    lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)
        title = "Доходы"

        rvIncome = findViewById(R.id.rvIncome)
        fabAdd = findViewById(R.id.fabAddIncome)

        rvIncome.layoutManager = LinearLayoutManager(this)
        val incomeRVAdapter = IncomeRVAdapter(this, this, this)

        rvIncome.adapter = incomeRVAdapter

        vmIncome = ViewModelProvider(
                this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(IncomeViewModel::class.java)

        val vmCategories: CategoryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CategoryViewModel::class.java)

        vmCategories.allCategories.observe(this, Observer { list ->
            list?.let {
                incomeRVAdapter.setCategories(it)
            }
        })

        vmIncome.allIncome.observe(this, Observer { list ->
            list?.let {
                incomeRVAdapter.updateList(it)
            }
        })

        fabAdd.setOnClickListener{
            val intent = Intent(this@IncomeActivity, AddChangeIncomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onIncomeClick(income: Income) {
        val intent = Intent(this@IncomeActivity, AddChangeIncomeActivity::class.java)
        intent.putExtra("action", "edit")
        intent.putExtra("sum", income.sum)
        intent.putExtra("category", income.category)
        intent.putExtra("date", income.date)
        intent.putExtra("title", income.title)
        intent.putExtra("comment", income.comment)
        intent.putExtra("id", income.id)
        startActivity(intent)
    }

    override fun onIncomeLongClick(income: Income) {
        vmIncome.deleteIncome(income)
        Toast.makeText(this, "Данные о доходах удалены", Toast.LENGTH_LONG).show()
    }
}