package com.strikes.busgapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strikes.busgapp.data.model.Category

@Entity(tableName = "daily_limits")
data class DailyLimit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val enabled: Boolean = true,
    val category: Category? = null, // null means global daily limit
    val isMonthly: Boolean = false // false = daily, true = monthly
)

