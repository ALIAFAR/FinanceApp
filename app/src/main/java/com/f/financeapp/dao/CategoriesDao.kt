package com.f.financeapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.f.financeapp.entities.Category

@Dao
interface CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategories(data: List<Category>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category : Category)

    @Delete
    suspend fun delete(category : Category)

    @Query("Select * from categoriesTable order by id ASC")
    fun getAll(): LiveData<List<Category>>

    @Update
    suspend fun update(category : Category)

    @Query("SELECT * FROM categoriesTable WHERE id = :id")
    fun getCategoryById(id: Int): Category?
}