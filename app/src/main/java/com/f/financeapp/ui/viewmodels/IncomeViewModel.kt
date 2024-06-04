package com.f.financeapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.f.financeapp.data.AppDatabase
import com.f.financeapp.data.repositories.IncomeRepository
import com.f.financeapp.entities.Income
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IncomeViewModel(application: Application) : AndroidViewModel(application) {

    val allIncome: LiveData<List<Income>>
    val repository: IncomeRepository

    init {
        val dao = AppDatabase.getDatabase(application).getIncomeDao()
        repository = IncomeRepository(dao)
        allIncome = repository.allIncome
    }

    fun addIncome(income: Income) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(income)
    }

    fun updateIncome(income: Income) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(income)
    }

    fun deleteIncome(income: Income) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(income)
    }
}