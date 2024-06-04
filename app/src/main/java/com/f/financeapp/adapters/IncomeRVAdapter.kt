package com.f.financeapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.R
import com.f.financeapp.entities.Category
import com.f.financeapp.entities.Income
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IncomeRVAdapter(
    val context: Context,
    val incomeClickInterface: IncomeClickInterface,
    val incomeLongClickInterface: IncomeLongClickInterface
) : RecyclerView.Adapter<IncomeRVAdapter.ViewHolder>() {

    private val allIncome = ArrayList<Income>()
    private val categories = ArrayList<Category>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIncomeTitle = itemView.findViewById<TextView>(R.id.tvIncomeTitle)
        val tvSum = itemView.findViewById<TextView>(R.id.tvSum)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val tvComment = itemView.findViewById<TextView>(R.id.tvComment)
        val tvCategory = itemView.findViewById<TextView>(R.id.tvCategory)
        val ivCategoryIcon = itemView.findViewById<ImageView>(R.id.ivCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.income_rv_item, parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allIncome.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val income = allIncome[position]

        holder.tvIncomeTitle.text = income.title
        val decimalFormat = DecimalFormat("0.#")
        holder.tvSum.text = decimalFormat.format(income.sum) + " руб."
        val date = LocalDate.parse(income.date, DateTimeFormatter.ISO_DATE)
        holder.tvDate.text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        holder.tvComment.text = income.comment
        val category: Category = categories.first { it.id == income.category }
        holder.tvCategory.text = category.name
        holder.ivCategoryIcon.setImageResource(category.iconResource)

        holder.itemView.setOnClickListener{
            incomeClickInterface.onIncomeClick(income)
        }

        holder.itemView.setOnLongClickListener {
            incomeLongClickInterface.onIncomeLongClick(income)
            true
        }
    }

    fun setCategories(newList: List<Category>){
        categories.clear()
        categories.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Income>){
        allIncome.clear()
        allIncome.addAll(newList)
        notifyDataSetChanged()
    }
}

interface IncomeClickInterface {
    fun onIncomeClick(income: Income)
}

interface IncomeLongClickInterface {
    fun onIncomeLongClick(income: Income)
}
