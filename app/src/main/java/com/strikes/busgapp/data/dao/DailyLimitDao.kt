package com.strikes.busgapp.data.dao

import androidx.room.*
import com.strikes.busgapp.data.entity.DailyLimit
import com.strikes.busgapp.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLimitDao {
    @Query("SELECT * FROM daily_limits ORDER BY category ASC")
    fun getAllLimits(): Flow<List<DailyLimit>>

    @Query("SELECT * FROM daily_limits WHERE category IS NULL AND isMonthly = 0 LIMIT 1")
    fun getGlobalDailyLimit(): Flow<DailyLimit?>

    @Query("SELECT * FROM daily_limits WHERE category = :category LIMIT 1")
    suspend fun getLimitByCategory(category: Category): DailyLimit?

    @Query("SELECT * FROM daily_limits WHERE id = :id")
    suspend fun getLimitById(id: Long): DailyLimit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimit(limit: DailyLimit): Long

    @Update
    suspend fun updateLimit(limit: DailyLimit)

    @Delete
    suspend fun deleteLimit(limit: DailyLimit)
}

