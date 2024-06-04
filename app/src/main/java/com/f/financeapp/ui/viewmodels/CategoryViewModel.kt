package com.f.financeapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.f.financeapp.data.AppDatabase
import com.f.financeapp.data.repositories.CategoryRepository
import com.f.financeapp.entities.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    val allCategories: LiveData<List<Category>>
    val repository: CategoryRepository

    init {
        val dao = AppDatabase.getDatabase(application).getCategoriesDao()
        repository = CategoryRepository(dao)
        allCategories = repository.allCategories
    }

    fun addCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(category)
    }

    fun getCategoryById(categoryId: Int): MutableLiveData<Category?> {
        val result = MutableLiveData<Category?>()
        viewModelScope.launch(Dispatchers.IO) {
            val category = repository.getCategoryById(categoryId)
            result.postValue(category)
        }
        return result
    }

}