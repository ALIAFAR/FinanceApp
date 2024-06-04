package com.f.financeapp.extensions

import android.content.res.Resources
import androidx.annotation.ArrayRes
import com.f.financeapp.entities.IconForCategory

fun @receiver:ArrayRes Int.toCategoryIconList(resources: Resources?): List<IconForCategory> =
    mutableListOf<IconForCategory>().apply {
        resources?.let {
            val categoryIcons = it.obtainTypedArray(this@toCategoryIconList)
            for (i in 0 until categoryIcons.length()) {
                val resId = categoryIcons.getResourceId(i, -1)
                val resEntryName = it.getResourceEntryName(resId)
                this.add(IconForCategory(resId, resEntryName))
            }
            categoryIcons.recycle()
        }
    }