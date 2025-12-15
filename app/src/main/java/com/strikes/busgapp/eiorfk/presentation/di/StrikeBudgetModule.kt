package com.strikes.busgapp.eiorfk.presentation.di

import com.strikes.busgapp.eiorfk.data.repo.StrikeBudgetRepository
import com.strikes.busgapp.eiorfk.data.shar.StrikeBudgetSharedPreference
import com.strikes.busgapp.eiorfk.data.utils.StrikeBudgetPushToken
import com.strikes.busgapp.eiorfk.data.utils.StrikeBudgetSystemService
import com.strikes.busgapp.eiorfk.domain.usecases.StrikeBudgetGetAllUseCase
import com.strikes.busgapp.eiorfk.presentation.pushhandler.StrikeBudgetPushHandler
import com.strikes.busgapp.eiorfk.presentation.ui.load.StrikeBudgetLoadViewModel
import com.strikes.busgapp.eiorfk.presentation.ui.view.StrikeBudgetViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val strikeBudgetModule = module {
    factory {
        StrikeBudgetPushHandler()
    }
    single {
        StrikeBudgetRepository()
    }
    single {
        StrikeBudgetSharedPreference(get())
    }
    factory {
        StrikeBudgetPushToken()
    }
    factory {
        StrikeBudgetSystemService(get())
    }
    factory {
        StrikeBudgetGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        StrikeBudgetViFun(get())
    }
    viewModel {
        StrikeBudgetLoadViewModel(get(), get(), get())
    }
}