package com.strikes.busgapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.TransactionType

@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val amount: Double? = null,
    val category: Category
)

