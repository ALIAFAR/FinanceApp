package com.f.financeapp.data.repositories

import androidx.lifecycle.LiveData
import com.f.financeapp.dao.CategoriesDao
import com.f.financeapp.entities.Category


class CategoryRepository(private val categoriesDao: CategoriesDao) {
    val allCategories: LiveData<List<Category>> = categoriesDao.getAll()

    suspend fun insert(category: Category){
        categoriesDao.insert(category)
    }

    suspend fun update(category: Category){
        categoriesDao.update(category)
    }

    suspend fun delete(category: Category){
        categoriesDao.delete(category)
    }

    fun getCategoryById(categoryId: Int): Category? {
        return categoriesDao.getCategoryById(categoryId)
    }
}