package com.f.financeapp.data.repositories

import androidx.lifecycle.LiveData
import com.f.financeapp.dao.ExpensesDao
import com.f.financeapp.entities.Expense

class ExpenseRepository(private val expensesDao: ExpensesDao) {
    val allExpenses: LiveData<List<Expense>> = expensesDao.getAll()

    suspend fun insert(expense: Expense){
        expensesDao.insert(expense)
    }

    suspend fun update(expense: Expense){
        expensesDao.update(expense)
    }

    suspend fun delete(expense: Expense){
        expensesDao.delete(expense)
    }
}