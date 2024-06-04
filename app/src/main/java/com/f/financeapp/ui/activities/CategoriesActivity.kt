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
import com.f.financeapp.adapters.CategoriesRVAdapter
import com.f.financeapp.adapters.CategoryClickInterface
import com.f.financeapp.adapters.CategoryLongClickInterface
import com.f.financeapp.entities.Category
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CategoriesActivity : AppCompatActivity(), CategoryClickInterface, CategoryLongClickInterface {

    lateinit var vmCategories: CategoryViewModel
    lateinit var rvCategories: RecyclerView
    lateinit var fabAdd: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        title = "Категории"

        rvCategories = findViewById(R.id.rvCategories)
        fabAdd = findViewById(R.id.fabAddCategory)

        rvCategories.layoutManager = LinearLayoutManager(this)
        val categoriesRVAdapter = CategoriesRVAdapter(this, this, this)

        rvCategories.adapter = categoriesRVAdapter

        vmCategories = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CategoryViewModel::class.java)

        vmCategories.allCategories.observe(this, Observer { list ->
            list?.let {
                categoriesRVAdapter.updateList(it)
            }
        })

        fabAdd.setOnClickListener{
            val intent = Intent(this@CategoriesActivity, AddChangeCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCategoryClick(category: Category) {
        val intent = Intent(this@CategoriesActivity, AddChangeCategoryActivity::class.java)
        intent.putExtra("action", "edit")
        intent.putExtra("name", category.name)
        intent.putExtra("iconResource", category.iconResource)
        intent.putExtra("id", category.id)
        startActivity(intent)
    }

    override fun onCategoryLongClick(category: Category) {
        vmCategories.deleteCategory(category)
        Toast.makeText(this, "Категория удалена", Toast.LENGTH_LONG).show()
    }
}