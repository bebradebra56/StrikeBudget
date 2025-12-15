package com.strikes.busgapp.data.repository

import com.strikes.busgapp.data.dao.DailyLimitDao
import com.strikes.busgapp.data.entity.DailyLimit
import com.strikes.busgapp.data.model.Category
import kotlinx.coroutines.flow.Flow

class DailyLimitRepository(private val dailyLimitDao: DailyLimitDao) {
    fun getAllLimits(): Flow<List<DailyLimit>> = dailyLimitDao.getAllLimits()

    fun getGlobalDailyLimit(): Flow<DailyLimit?> = dailyLimitDao.getGlobalDailyLimit()

    suspend fun getLimitByCategory(category: Category): DailyLimit? =
        dailyLimitDao.getLimitByCategory(category)

    suspend fun getLimitById(id: Long): DailyLimit? = dailyLimitDao.getLimitById(id)

    suspend fun insertLimit(limit: DailyLimit): Long = dailyLimitDao.insertLimit(limit)

    suspend fun updateLimit(limit: DailyLimit) = dailyLimitDao.updateLimit(limit)

    suspend fun deleteLimit(limit: DailyLimit) = dailyLimitDao.deleteLimit(limit)
}

