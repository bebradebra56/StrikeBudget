package com.strikes.busgapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.RecurringFrequency

@Entity(tableName = "recurring_transactions")
data class RecurringTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val category: Category,
    val frequency: RecurringFrequency,
    val nextPaymentDate: Long,
    val notificationEnabled: Boolean = true
)

