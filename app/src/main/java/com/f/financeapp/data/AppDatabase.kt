package com.f.financeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.f.financeapp.R
import com.f.financeapp.dao.CategoriesDao
import com.f.financeapp.dao.ExpensesDao
import com.f.financeapp.dao.IncomeDao
import com.f.financeapp.entities.Category
import com.f.financeapp.entities.Expense
import com.f.financeapp.entities.Income
import com.f.financeapp.ioThread

@Database(entities = [Expense::class, Income::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getExpensesDao(): ExpensesDao
    abstract fun getCategoriesDao(): CategoriesDao
    abstract fun getIncomeDao(): IncomeDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "finances_database")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ioThread {
                            getDatabase(context).getCategoriesDao().insertCategories(PREPOPULATE_CATEGORIES)
                            getDatabase(context).getExpensesDao().insertExpenses(PREPOPULATE_EXPENSES)
                            getDatabase(context).getIncomeDao().insertIncome(PREPOPULATE_INCOME)
                        }
                    }
                })
                .build()

        val PREPOPULATE_CATEGORIES = listOf(
            Category("Другое", R.drawable.ic_category_other),
            Category("Продукты домой", R.drawable.ic_category_products),
            Category("Обед", R.drawable.ic_category_dinner),
            Category("Дом", R.drawable.ic_category_house_1),
            Category("Подарки", R.drawable.ic_category_gift),
            Category("Медицина", R.drawable.ic_category_medical),
            Category("Связь", R.drawable.ic_category_phone),
            Category("Транспорт", R.drawable.ic_category_traffic),
            Category("Развлечения", R.drawable.ic_category_video_game),
            Category("Сладкое", R.drawable.ic_category_dessert),
            Category("Спорт", R.drawable.ic_category_fitness),
            Category("Работа", R.drawable.ic_category_salary),
            Category("Инвестиции", R.drawable.ic_category_investment),
            Category("Банковский вклад", R.drawable.ic_category_deposit),
        )

        val PREPOPULATE_EXPENSES = listOf(
            Expense(1000.0, 1, "2024-04-29", "Расход 1", "комментарий"),
            Expense(1000.0, 2, "2024-05-03", "Расход 2", "комментарий"),
            Expense(2000.0, 3, "2024-05-03", "Расход 3", "комментарий"),
        )

        val PREPOPULATE_INCOME = listOf(
            Income(12000.0, 12, "2024-05-03", "Доход 1", "комментарий"),
            Income(11000.0, 13, "2024-05-03", "Доход 2", "комментарий"),
            Income(5000.0, 13, "2024-04-29", "Доход 3", "комментарий"),
        )
    }
}