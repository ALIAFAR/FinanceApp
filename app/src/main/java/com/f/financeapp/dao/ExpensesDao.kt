package com.f.financeapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.f.financeapp.entities.Expense

@Dao
interface ExpensesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertExpenses(data: List<Expense>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense : Expense)

    @Delete
    suspend fun delete(expense : Expense)

    @Query("Select * from expensesTable order by date DESC")
    fun getAll(): LiveData<List<Expense>>

    @Update
    suspend fun update(expense : Expense)
}