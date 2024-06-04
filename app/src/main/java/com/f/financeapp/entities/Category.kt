package com.f.financeapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoriesTable")
class Category (
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "iconResource") var iconResource: Int,
    ) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
