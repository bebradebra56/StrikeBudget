package com.strikes.busgapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.TransactionType

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val note: String = "",
    val walletId: Long = 1, // Default wallet
    val timestamp: Long = System.currentTimeMillis()
)

