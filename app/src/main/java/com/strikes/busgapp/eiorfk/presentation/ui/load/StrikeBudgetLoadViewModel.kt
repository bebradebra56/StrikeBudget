package com.strikes.busgapp.eiorfk.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strikes.busgapp.eiorfk.data.shar.StrikeBudgetSharedPreference
import com.strikes.busgapp.eiorfk.data.utils.StrikeBudgetSystemService
import com.strikes.busgapp.eiorfk.domain.usecases.StrikeBudgetGetAllUseCase
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetAppsFlyerState
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StrikeBudgetLoadViewModel(
    private val strikeBudgetGetAllUseCase: StrikeBudgetGetAllUseCase,
    private val strikeBudgetSharedPreference: StrikeBudgetSharedPreference,
    private val strikeBudgetSystemService: StrikeBudgetSystemService
) : ViewModel() {

    private val _strikeBudgetHomeScreenState: MutableStateFlow<StrikeBudgetHomeScreenState> =
        MutableStateFlow(StrikeBudgetHomeScreenState.StrikeBudgetLoading)
    val strikeBudgetHomeScreenState = _strikeBudgetHomeScreenState.asStateFlow()

    private var strikeBudgetGetApps = false


    init {
        viewModelScope.launch {
            when (strikeBudgetSharedPreference.strikeBudgetAppState) {
                0 -> {
                    if (strikeBudgetSystemService.strikeBudgetIsOnline()) {
                        StrikeBudgetApplication.strikeBudgetConversionFlow.collect {
                            when(it) {
                                StrikeBudgetAppsFlyerState.StrikeBudgetDefault -> {}
                                StrikeBudgetAppsFlyerState.StrikeBudgetError -> {
                                    strikeBudgetSharedPreference.strikeBudgetAppState = 2
                                    _strikeBudgetHomeScreenState.value =
                                        StrikeBudgetHomeScreenState.StrikeBudgetError
                                    strikeBudgetGetApps = true
                                }
                                is StrikeBudgetAppsFlyerState.StrikeBudgetSuccess -> {
                                    if (!strikeBudgetGetApps) {
                                        strikeBudgetGetData(it.strikeBudgetData)
                                        strikeBudgetGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _strikeBudgetHomeScreenState.value =
                            StrikeBudgetHomeScreenState.StrikeBudgetNotInternet
                    }
                }
                1 -> {
                    if (strikeBudgetSystemService.strikeBudgetIsOnline()) {
                        if (StrikeBudgetApplication.STRIKE_BUDGET_FB_LI != null) {
                            _strikeBudgetHomeScreenState.value =
                                StrikeBudgetHomeScreenState.StrikeBudgetSuccess(
                                    StrikeBudgetApplication.STRIKE_BUDGET_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > strikeBudgetSharedPreference.strikeBudgetExpired) {
                            Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Current time more then expired, repeat request")
                            StrikeBudgetApplication.strikeBudgetConversionFlow.collect {
                                when(it) {
                                    StrikeBudgetAppsFlyerState.StrikeBudgetDefault -> {}
                                    StrikeBudgetAppsFlyerState.StrikeBudgetError -> {
                                        _strikeBudgetHomeScreenState.value =
                                            StrikeBudgetHomeScreenState.StrikeBudgetSuccess(
                                                strikeBudgetSharedPreference.strikeBudgetSavedUrl
                                            )
                                        strikeBudgetGetApps = true
                                    }
                                    is StrikeBudgetAppsFlyerState.StrikeBudgetSuccess -> {
                                        if (!strikeBudgetGetApps) {
                                            strikeBudgetGetData(it.strikeBudgetData)
                                            strikeBudgetGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Current time less then expired, use saved url")
                            _strikeBudgetHomeScreenState.value =
                                StrikeBudgetHomeScreenState.StrikeBudgetSuccess(
                                    strikeBudgetSharedPreference.strikeBudgetSavedUrl
                                )
                        }
                    } else {
                        _strikeBudgetHomeScreenState.value =
                            StrikeBudgetHomeScreenState.StrikeBudgetNotInternet
                    }
                }
                2 -> {
                    _strikeBudgetHomeScreenState.value =
                        StrikeBudgetHomeScreenState.StrikeBudgetError
                }
            }
        }
    }


    private suspend fun strikeBudgetGetData(conversation: MutableMap<String, Any>?) {
        val strikeBudgetData = strikeBudgetGetAllUseCase.invoke(conversation)
        if (strikeBudgetSharedPreference.strikeBudgetAppState == 0) {
            if (strikeBudgetData == null) {
                strikeBudgetSharedPreference.strikeBudgetAppState = 2
                _strikeBudgetHomeScreenState.value =
                    StrikeBudgetHomeScreenState.StrikeBudgetError
            } else {
                strikeBudgetSharedPreference.strikeBudgetAppState = 1
                strikeBudgetSharedPreference.apply {
                    strikeBudgetExpired = strikeBudgetData.strikeBudgetExpires
                    strikeBudgetSavedUrl = strikeBudgetData.strikeBudgetUrl
                }
                _strikeBudgetHomeScreenState.value =
                    StrikeBudgetHomeScreenState.StrikeBudgetSuccess(strikeBudgetData.strikeBudgetUrl)
            }
        } else  {
            if (strikeBudgetData == null) {
                _strikeBudgetHomeScreenState.value =
                    StrikeBudgetHomeScreenState.StrikeBudgetSuccess(strikeBudgetSharedPreference.strikeBudgetSavedUrl)
            } else {
                strikeBudgetSharedPreference.apply {
                    strikeBudgetExpired = strikeBudgetData.strikeBudgetExpires
                    strikeBudgetSavedUrl = strikeBudgetData.strikeBudgetUrl
                }
                _strikeBudgetHomeScreenState.value =
                    StrikeBudgetHomeScreenState.StrikeBudgetSuccess(strikeBudgetData.strikeBudgetUrl)
            }
        }
    }


    sealed class StrikeBudgetHomeScreenState {
        data object StrikeBudgetLoading : StrikeBudgetHomeScreenState()
        data object StrikeBudgetError : StrikeBudgetHomeScreenState()
        data class StrikeBudgetSuccess(val data: String) : StrikeBudgetHomeScreenState()
        data object StrikeBudgetNotInternet: StrikeBudgetHomeScreenState()
    }
}