package com.strikes.busgapp.eiorfk

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication

class StrikeBudgetGlobalLayoutUtil {

    private var strikeBudgetMChildOfContent: View? = null
    private var strikeBudgetUsableHeightPrevious = 0

    fun strikeBudgetAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        strikeBudgetMChildOfContent = content.getChildAt(0)

        strikeBudgetMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val strikeBudgetUsableHeightNow = strikeBudgetComputeUsableHeight()
        if (strikeBudgetUsableHeightNow != strikeBudgetUsableHeightPrevious) {
            val strikeBudgetUsableHeightSansKeyboard = strikeBudgetMChildOfContent?.rootView?.height ?: 0
            val strikeBudgetHeightDifference = strikeBudgetUsableHeightSansKeyboard - strikeBudgetUsableHeightNow

            if (strikeBudgetHeightDifference > (strikeBudgetUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(StrikeBudgetApplication.strikeBudgetInputMode)
            } else {
                activity.window.setSoftInputMode(StrikeBudgetApplication.strikeBudgetInputMode)
            }
//            mChildOfContent?.requestLayout()
            strikeBudgetUsableHeightPrevious = strikeBudgetUsableHeightNow
        }
    }

    private fun strikeBudgetComputeUsableHeight(): Int {
        val r = Rect()
        strikeBudgetMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}