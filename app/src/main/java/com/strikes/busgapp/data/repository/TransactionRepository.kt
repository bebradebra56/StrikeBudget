package com.strikes.busgapp.data.repository

import com.strikes.busgapp.data.dao.TransactionDao
import com.strikes.busgapp.data.entity.Transaction
import com.strikes.busgapp.data.model.Category
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun getTransactionById(id: Long): Transaction? = transactionDao.getTransactionById(id)

    fun getTransactionsByDateRange(startTime: Long, endTime: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startTime, endTime)

    fun getTransactionsByCategory(category: Category): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(category)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteAllTransactions() = transactionDao.deleteAllTransactions()
}

