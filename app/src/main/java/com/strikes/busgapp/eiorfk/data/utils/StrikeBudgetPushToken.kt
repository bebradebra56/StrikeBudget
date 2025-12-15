package com.strikes.busgapp.eiorfk.data.utils

import android.util.Log
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class StrikeBudgetPushToken {

    suspend fun strikeBudgetGetToken(
        strikeBudgetMaxAttempts: Int = 7,
        strikeBudgetDelayMs: Long = 1500
    ): String {

        repeat(strikeBudgetMaxAttempts - 1) {
            try {
                val strikeBudgetToken = FirebaseMessaging.getInstance().token.await()
                return strikeBudgetToken
            } catch (e: Exception) {
                Log.e(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(strikeBudgetDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}