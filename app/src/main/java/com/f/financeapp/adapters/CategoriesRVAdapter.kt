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

class CategoriesRVAdapter(
    val context: Context,
    val categoryClickInterface: CategoryClickInterface,
    val categoryLongClickInterface: CategoryLongClickInterface
    ) : RecyclerView.Adapter<CategoriesRVAdapter.ViewHolder>() {

    private val allCategories = ArrayList<Category>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCategoryName = itemView.findViewById<TextView>(R.id.tvCategoryName)
        val ivCategoryIcon = itemView.findViewById<ImageView>(R.id.ivCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.category_rv_item, parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = allCategories[position]

        holder.tvCategoryName.text = category.name
        holder.ivCategoryIcon.setImageResource(category.iconResource)

        holder.itemView.setOnClickListener{
            categoryClickInterface.onCategoryClick(category)
        }

        holder.itemView.setOnLongClickListener {
            categoryLongClickInterface.onCategoryLongClick(category)
            true
        }
    }

    fun updateList(newList: List<Category>){
        allCategories.clear()
        allCategories.addAll(newList)
        notifyDataSetChanged()
    }
}

interface CategoryClickInterface {
    fun onCategoryClick(category: Category)
}

interface CategoryLongClickInterface {
    fun onCategoryLongClick(category: Category)
}