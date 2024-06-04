package com.f.financeapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.entities.IconForCategory
import com.f.financeapp.R
import com.f.financeapp.adapters.CategoriesIconsRVAdapter
import com.f.financeapp.adapters.IconClickInterface
import com.f.financeapp.entities.Category
import com.f.financeapp.extensions.toCategoryIconList
import com.f.financeapp.ui.viewmodels.CategoryViewModel


class AddChangeCategoryActivity : AppCompatActivity(), IconClickInterface {
    lateinit var etCategoryName: EditText
    lateinit var btnSave: Button
    lateinit var rvCategoriesIcons: RecyclerView
    lateinit var ivChosenIconCategory: ImageView

    lateinit var vmCategory: CategoryViewModel

    var categoryId = -1
    var iconResource = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_change_category)

        vmCategory = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CategoryViewModel::class.java)

        etCategoryName = findViewById(R.id.etCategoryName)
        btnSave = findViewById(R.id.btnSave)
        ivChosenIconCategory = findViewById(R.id.ivChosenIconCategory)

        val action = intent.getStringExtra("action")
        if (action.equals("edit")){
            title = "Изменение категории"
            val name = intent.getStringExtra("name")
            iconResource = intent.getIntExtra("iconResource", R.drawable.ic_category_other)
            ivChosenIconCategory.setImageResource(iconResource)
            val isIncomeCategory = intent.getBooleanExtra("isIncomeCategory", false)
            categoryId = intent.getIntExtra("id", -1)
            etCategoryName.setText(name)
        } else {
            iconResource = R.drawable.ic_category_other
            ivChosenIconCategory.setImageResource(iconResource)
            title = "Добавление категории"
            btnSave.text = "Сохранить"
        }

        btnSave.setOnClickListener{
            try {
                if (etCategoryName.text.toString() == ""){
                    Toast.makeText(this, "Не указано навзание категории!", Toast.LENGTH_SHORT).show()
                    throw java.lang.IllegalArgumentException("Не указано навзание категории!")
                }
                val name = etCategoryName.text.toString()

                if (action.equals("edit")) {
                    val updatedCategory = Category(name, iconResource)
                    updatedCategory.id = categoryId
                    vmCategory.updateCategory(updatedCategory)
                    Toast.makeText(this, "Категория обновлена", Toast.LENGTH_LONG).show()
                } else {
                    vmCategory.addCategory(Category(name, iconResource))
                    Toast.makeText(this, "Категория $name добавлена", Toast.LENGTH_LONG).show()
                }
                val intent = Intent(this, FinanceActivity::class.java).apply {
                    putExtra("fragment", "CategoriesFragment")
                }
                startActivity(intent)
            } catch (e: java.lang.Exception){

            }
        }

        rvCategoriesIcons = findViewById(R.id.rvCategoriesIcons)
        rvCategoriesIcons.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val categoriesIconsAdapter = CategoriesIconsRVAdapter(this, this)

        val categoryIconList = R.array.category_icon.toCategoryIconList(resources)

        rvCategoriesIcons.adapter = categoriesIconsAdapter

        categoriesIconsAdapter.updateList(categoryIconList)
    }

    override fun onIconClick(iconForCategory: IconForCategory) {
        iconResource = iconForCategory.iconRes
        ivChosenIconCategory.setImageResource(iconResource)
    }
}