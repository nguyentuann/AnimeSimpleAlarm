package com.app.base.di

import com.app.base.viewModel.ListAlarmViewModel
import com.app.base.viewModel.NewAlarmViewModel
import com.app.base.viewModel.StopWatchViewModel
import com.app.base.viewModel.TimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ListAlarmViewModel(get()) }
    viewModel { NewAlarmViewModel() }
    viewModel {
        TimerViewModel(
            get(),
        )
    }
    viewModel {
        StopWatchViewModel(
            get()
        )
    }
}
