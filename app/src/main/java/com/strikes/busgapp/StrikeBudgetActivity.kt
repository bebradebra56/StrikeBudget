package com.strikes.busgapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.strikes.busgapp.eiorfk.StrikeBudgetGlobalLayoutUtil
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication
import com.strikes.busgapp.eiorfk.presentation.pushhandler.StrikeBudgetPushHandler
import com.strikes.busgapp.eiorfk.strikeBudgetSetupSystemBars
import org.koin.android.ext.android.inject

class StrikeBudgetActivity : AppCompatActivity() {

    private val strikeBudgetPushHandler by inject<StrikeBudgetPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        strikeBudgetSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_strike_budget)

        val strikeBudgetRootView = findViewById<View>(android.R.id.content)
        StrikeBudgetGlobalLayoutUtil().strikeBudgetAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(strikeBudgetRootView) { strikeBudgetView, strikeBudgetInsets ->
            val strikeBudgetSystemBars = strikeBudgetInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val strikeBudgetDisplayCutout = strikeBudgetInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val strikeBudgetIme = strikeBudgetInsets.getInsets(WindowInsetsCompat.Type.ime())


            val strikeBudgetTopPadding = maxOf(strikeBudgetSystemBars.top, strikeBudgetDisplayCutout.top)
            val strikeBudgetLeftPadding = maxOf(strikeBudgetSystemBars.left, strikeBudgetDisplayCutout.left)
            val strikeBudgetRightPadding = maxOf(strikeBudgetSystemBars.right, strikeBudgetDisplayCutout.right)
            window.setSoftInputMode(StrikeBudgetApplication.strikeBudgetInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "ADJUST PUN")
                val strikeBudgetBottomInset = maxOf(strikeBudgetSystemBars.bottom, strikeBudgetDisplayCutout.bottom)

                strikeBudgetView.setPadding(strikeBudgetLeftPadding, strikeBudgetTopPadding, strikeBudgetRightPadding, 0)

                strikeBudgetView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = strikeBudgetBottomInset
                }
            } else {
                Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "ADJUST RESIZE")

                val strikeBudgetBottomInset = maxOf(strikeBudgetSystemBars.bottom, strikeBudgetDisplayCutout.bottom, strikeBudgetIme.bottom)

                strikeBudgetView.setPadding(strikeBudgetLeftPadding, strikeBudgetTopPadding, strikeBudgetRightPadding, 0)

                strikeBudgetView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = strikeBudgetBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Activity onCreate()")
        strikeBudgetPushHandler.strikeBudgetHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            strikeBudgetSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        strikeBudgetSetupSystemBars()
    }
}