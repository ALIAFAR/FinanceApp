package com.f.financeapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.f.financeapp.data.AppDatabase
import com.f.financeapp.data.repositories.ExpenseRepository
import com.f.financeapp.entities.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpensesViewModel(application: Application) : AndroidViewModel(application) {

    val allExpenses: LiveData<List<Expense>>
    val repository: ExpenseRepository

    init {
        val dao = AppDatabase.getDatabase(application).getExpensesDao()
        repository = ExpenseRepository(dao)
        allExpenses = repository.allExpenses
    }

    fun addExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(expense)
    }

    fun updateExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(expense)
    }
}