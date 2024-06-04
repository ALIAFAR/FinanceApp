package com.f.financeapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.f.financeapp.entities.Expense
import com.f.financeapp.entities.Income

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIncome(data: List<Income>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(income : Income)

    @Delete
    suspend fun delete(income : Income)

    @Query("Select * from incomeTable order by date DESC")
    fun getAll(): LiveData<List<Income>>

    @Update
    suspend fun update(income : Income)
}