package com.strikes.busgapp.eiorfk.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication

class StrikeBudgetPushHandler {
    fun strikeBudgetHandlePush(extras: Bundle?) {
        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = strikeBudgetBundleToMap(extras)
            Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    StrikeBudgetApplication.STRIKE_BUDGET_FB_LI = map["url"]
                    Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Push data no!")
        }
    }

    private fun strikeBudgetBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}