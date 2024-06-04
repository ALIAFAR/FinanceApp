package com.f.financeapp.data.repositories

import androidx.lifecycle.LiveData
import com.f.financeapp.dao.ExpensesDao
import com.f.financeapp.dao.IncomeDao
import com.f.financeapp.entities.Expense
import com.f.financeapp.entities.Income


class IncomeRepository(private val incomeDao: IncomeDao) {
    val allIncome: LiveData<List<Income>> = incomeDao.getAll()

    suspend fun insert(income: Income){
        incomeDao.insert(income)
    }

    suspend fun update(income: Income){
        incomeDao.update(income)
    }

    suspend fun delete(income: Income){
        incomeDao.delete(income)
    }
}