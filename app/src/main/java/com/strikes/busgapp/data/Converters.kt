package com.strikes.busgapp.data

import androidx.room.TypeConverter
import com.strikes.busgapp.data.model.Category
import com.strikes.busgapp.data.model.RecurringFrequency
import com.strikes.busgapp.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromRecurringFrequency(value: RecurringFrequency): String = value.name

    @TypeConverter
    fun toRecurringFrequency(value: String): RecurringFrequency = RecurringFrequency.valueOf(value)

    @TypeConverter
    fun fromCategoryNullable(value: Category?): String? = value?.name

    @TypeConverter
    fun toCategoryNullable(value: String?): Category? = value?.let { Category.valueOf(it) }
}

