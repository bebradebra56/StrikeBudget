package com.strikes.busgapp.eiorfk.domain.usecases

import android.util.Log
import com.strikes.busgapp.eiorfk.data.repo.StrikeBudgetRepository
import com.strikes.busgapp.eiorfk.data.utils.StrikeBudgetPushToken
import com.strikes.busgapp.eiorfk.data.utils.StrikeBudgetSystemService
import com.strikes.busgapp.eiorfk.domain.model.StrikeBudgetEntity
import com.strikes.busgapp.eiorfk.domain.model.StrikeBudgetParam
import com.strikes.busgapp.eiorfk.presentation.app.StrikeBudgetApplication

class StrikeBudgetGetAllUseCase(
    private val strikeBudgetRepository: StrikeBudgetRepository,
    private val strikeBudgetSystemService: StrikeBudgetSystemService,
    private val strikeBudgetPushToken: StrikeBudgetPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : StrikeBudgetEntity?{
        val params = StrikeBudgetParam(
            strikeBudgetLocale = strikeBudgetSystemService.strikeBudgetGetLocale(),
            strikeBudgetPushToken = strikeBudgetPushToken.strikeBudgetGetToken(),
            strikeBudgetAfId = strikeBudgetSystemService.strikeBudgetGetAppsflyerId()
        )
        Log.d(StrikeBudgetApplication.STRIKE_BUDGET_MAIN_TAG, "Params for request: $params")
        return strikeBudgetRepository.strikeBudgetGetClient(params, conversion)
    }



}