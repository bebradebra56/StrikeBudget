package com.strikes.busgapp.eiorfk.data.shar

import android.content.Context
import androidx.core.content.edit

class StrikeBudgetSharedPreference(context: Context) {
    private val strikeBudgetPrefs = context.getSharedPreferences("strikeBudgetSharedPrefsAb", Context.MODE_PRIVATE)

    var strikeBudgetSavedUrl: String
        get() = strikeBudgetPrefs.getString(STRIKE_BUDGET_SAVED_URL, "") ?: ""
        set(value) = strikeBudgetPrefs.edit { putString(STRIKE_BUDGET_SAVED_URL, value) }

    var strikeBudgetExpired : Long
        get() = strikeBudgetPrefs.getLong(STRIKE_BUDGET_EXPIRED, 0L)
        set(value) = strikeBudgetPrefs.edit { putLong(STRIKE_BUDGET_EXPIRED, value) }

    var strikeBudgetAppState: Int
        get() = strikeBudgetPrefs.getInt(STRIKE_BUDGET_APPLICATION_STATE, 0)
        set(value) = strikeBudgetPrefs.edit { putInt(STRIKE_BUDGET_APPLICATION_STATE, value) }

    var strikeBudgetNotificationRequest: Long
        get() = strikeBudgetPrefs.getLong(STRIKE_BUDGET_NOTIFICAITON_REQUEST, 0L)
        set(value) = strikeBudgetPrefs.edit { putLong(STRIKE_BUDGET_NOTIFICAITON_REQUEST, value) }

    var strikeBudgetNotificationRequestedBefore: Boolean
        get() = strikeBudgetPrefs.getBoolean(STRIKE_BUDGET_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = strikeBudgetPrefs.edit { putBoolean(
            STRIKE_BUDGET_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val STRIKE_BUDGET_SAVED_URL = "strikeBudgetSavedUrl"
        private const val STRIKE_BUDGET_EXPIRED = "strikeBudgetExpired"
        private const val STRIKE_BUDGET_APPLICATION_STATE = "strikeBudgetApplicationState"
        private const val STRIKE_BUDGET_NOTIFICAITON_REQUEST = "strikeBudgetNotificationRequest"
        private const val STRIKE_BUDGET_NOTIFICATION_REQUEST_BEFORE = "strikeBudgetNotificationRequestedBefore"
    }
}