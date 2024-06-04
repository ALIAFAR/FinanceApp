package com.f.financeapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.f.financeapp.entities.IconForCategory
import com.f.financeapp.R

class CategoriesIconsRVAdapter(
    val context: Context,
    val iconClickInterface: IconClickInterface
    ) : RecyclerView.Adapter<CategoriesIconsRVAdapter.ViewHolder>() {

    private val allIcons = ArrayList<IconForCategory>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivCategoryIcon = itemView.findViewById<ImageView>(R.id.ivCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.category_icon_rv_item, parent, false
        )
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allIcons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icon = allIcons[position]

        holder.ivCategoryIcon.setImageResource(icon.iconRes)

        holder.itemView.setOnClickListener{
            iconClickInterface.onIconClick(icon)
        }
    }

    fun updateList(newList: List<IconForCategory>){
        allIcons.clear()
        allIcons.addAll(newList)
        notifyDataSetChanged()
    }
}

interface IconClickInterface {
    fun onIconClick(iconForCategory: IconForCategory)
}