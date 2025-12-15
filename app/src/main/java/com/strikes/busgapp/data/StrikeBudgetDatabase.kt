package com.strikes.busgapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.strikes.busgapp.data.dao.*
import com.strikes.busgapp.data.entity.*

@Database(
    entities = [
        Transaction::class,
        Template::class,
        RecurringTransaction::class,
        DailyLimit::class,
        Wallet::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StrikeBudgetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun templateDao(): TemplateDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract fun dailyLimitDao(): DailyLimitDao
    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile
        private var INSTANCE: StrikeBudgetDatabase? = null

        fun getDatabase(context: Context): StrikeBudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StrikeBudgetDatabase::class.java,
                    "strike_budget_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

