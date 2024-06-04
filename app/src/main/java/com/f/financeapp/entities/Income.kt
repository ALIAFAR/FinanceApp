package com.f.financeapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "incomeTable",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category"]
        )]
)
class Income(
    @ColumnInfo(name = "sum") val sum: Double,
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "comment") val comment: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}