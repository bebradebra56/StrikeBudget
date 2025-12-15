package com.strikes.busgapp.data.repository

import com.strikes.busgapp.data.dao.RecurringTransactionDao
import com.strikes.busgapp.data.entity.RecurringTransaction
import kotlinx.coroutines.flow.Flow

class RecurringTransactionRepository(private val recurringTransactionDao: RecurringTransactionDao) {
    fun getAllRecurringTransactions(): Flow<List<RecurringTransaction>> =
        recurringTransactionDao.getAllRecurringTransactions()

    suspend fun getRecurringTransactionById(id: Long): RecurringTransaction? =
        recurringTransactionDao.getRecurringTransactionById(id)

    suspend fun insertRecurringTransaction(recurringTransaction: RecurringTransaction): Long =
        recurringTransactionDao.insertRecurringTransaction(recurringTransaction)

    suspend fun updateRecurringTransaction(recurringTransaction: RecurringTransaction) =
        recurringTransactionDao.updateRecurringTransaction(recurringTransaction)

    suspend fun deleteRecurringTransaction(recurringTransaction: RecurringTransaction) =
        recurringTransactionDao.deleteRecurringTransaction(recurringTransaction)
}

