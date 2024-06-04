package com.f.financeapp.adapters

import com.f.financeapp.entities.Expense
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.R
import com.f.financeapp.entities.Category
import com.f.financeapp.ui.viewmodels.CategoryViewModel
import com.f.financeapp.ui.viewmodels.ExpensesViewModel
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExpenseRVAdapter(
    val context: Context,
    val expenseClickInterface: ExpenseClickInterface,
    val expenseLongClickInterface: ExpenseLongClickInterface,
) : RecyclerView.Adapter<ExpenseRVAdapter.ViewHolder>() {

    private val allExpenses = ArrayList<Expense>()
    private val categories = ArrayList<Category>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExpenseTitle = itemView.findViewById<TextView>(R.id.tvExpenseTitle)
        val tvSum = itemView.findViewById<TextView>(R.id.tvSum)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvComment = itemView.findViewById<TextView>(R.id.tvComment)
        val tvCategory = itemView.findViewById<TextView>(R.id.tvCategory)
        val ivCategoryIcon = itemView.findViewById<ImageView>(R.id.ivCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.expense_rv_item, parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allExpenses.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = allExpenses[position]

        holder.tvExpenseTitle.text = expense.title
        val decimalFormat = DecimalFormat("0.#")
        holder.tvSum.text = decimalFormat.format(expense.sum) + " руб."
        val date = LocalDate.parse(expense.date, DateTimeFormatter.ISO_DATE)
        holder.tvDate.text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        holder.tvComment.text = expense.comment
        val category: Category = categories.first { it.id == expense.category }
        holder.tvCategory.text = "${category.name}"
        holder.ivCategoryIcon.setImageResource(category.iconResource)

        holder.itemView.setOnClickListener{
            expenseClickInterface.onExpenseClick(expense)
        }

        holder.itemView.setOnLongClickListener {
            expenseLongClickInterface.onExpenseLongClick(expense)
            true
        }
    }

    fun setCategories(newList: List<Category>){
        categories.clear()
        categories.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Expense>){
        allExpenses.clear()
        allExpenses.addAll(newList)
        notifyDataSetChanged()
    }
}

interface ExpenseClickInterface {
    fun onExpenseClick(expense: Expense)
}

interface ExpenseLongClickInterface {
    fun onExpenseLongClick(expense: Expense)
}
