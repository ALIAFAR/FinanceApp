package com.f.financeapp.ui.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.f.financeapp.R
import com.f.financeapp.notifications.NotificationSender
import com.f.financeapp.ui.fragments.CategoriesFragment
import com.f.financeapp.ui.fragments.ExpensesFragment
import com.f.financeapp.ui.fragments.IncomeFragment
import com.f.financeapp.ui.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView



class FinanceActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        title = "Учет расходов и доходов"

        val fragmentName = intent.getStringExtra("fragment")

        if (fragmentName != null) {
            when (fragmentName) {
                "IncomeFragment" -> {
                    loadFragment(IncomeFragment())
                    title = "Доходы"
                }
                "CategoriesFragment" -> {
                    loadFragment(CategoriesFragment())
                    title = "Категории"
                }
                else -> loadFragment(ExpensesFragment())
            }
        } else {
            loadFragment(ExpensesFragment())
        }

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bm_expenses -> {
                    loadFragment(ExpensesFragment())
                    title = "Расходы"
                    true
                }
                R.id.bm_income -> {
                    loadFragment(IncomeFragment())
                    title = "Доходы"
                    true
                }
                R.id.bm_categories -> {
                    title = "Категории"
                    loadFragment(CategoriesFragment())
                    true
                }
                R.id.bm_settings -> {
                    loadFragment(SettingsFragment())
                    title = "Настройки"
                    true
                }
                else -> throw AssertionError()
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }


}