package com.f.financeapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.ui.activities.AddChangeCategoryActivity
import com.f.financeapp.R
import com.f.financeapp.adapters.CategoriesRVAdapter
import com.f.financeapp.adapters.CategoryClickInterface
import com.f.financeapp.adapters.CategoryLongClickInterface
import com.f.financeapp.entities.Category
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CategoriesFragment : Fragment(), CategoryClickInterface, CategoryLongClickInterface {

    lateinit var vmCategory: CategoryViewModel
    lateinit var rvCategories: RecyclerView
    lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        (activity as AppCompatActivity).supportActionBar?.title = "Категории"

        rvCategories = view.findViewById(R.id.rvCategories)
        fabAdd = view.findViewById(R.id.fabAddCategory)

        rvCategories.layoutManager = LinearLayoutManager(context)
        val categoriesRVAdapter = context?.let { CategoriesRVAdapter(it, this, this) }

        rvCategories.adapter = categoriesRVAdapter

        vmCategory = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(CategoryViewModel::class.java)

        vmCategory.allCategories.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                if (categoriesRVAdapter != null) {
                    categoriesRVAdapter.updateList(it)
                }
            }
        })

        fabAdd.setOnClickListener{
            val intent = Intent(context, AddChangeCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCategoryClick(category: Category) {
        val intent = Intent(context, AddChangeCategoryActivity::class.java)
        intent.putExtra("action", "edit")
        intent.putExtra("name", category.name)
        intent.putExtra("iconResource", category.iconResource)
        intent.putExtra("id", category.id)
        startActivity(intent)
    }

    override fun onCategoryLongClick(category: Category) {
        vmCategory.deleteCategory(category)
        Toast.makeText(context, "Категория удалена", Toast.LENGTH_LONG).show()
    }
}