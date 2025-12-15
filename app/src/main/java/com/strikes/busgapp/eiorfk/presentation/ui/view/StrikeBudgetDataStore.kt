package com.strikes.busgapp.eiorfk.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class StrikeBudgetDataStore : ViewModel(){
    val strikeBudgetViList: MutableList<StrikeBudgetVi> = mutableListOf()
    var strikeBudgetIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var strikeBudgetContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var strikeBudgetView: StrikeBudgetVi

}