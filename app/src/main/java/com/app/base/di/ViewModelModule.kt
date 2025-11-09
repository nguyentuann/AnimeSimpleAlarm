package com.app.base.di

import com.app.base.MainViewModel
import com.app.base.ui.home.NewAlarmViewModel
import com.app.base.ui.home.ListAlarmViewModel
import com.app.base.ui.quickalarm.QuickAlarmViewModel
import com.app.base.ui.stopwatch.StopWatchViewModel
import com.app.base.ui.timer.TimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { ListAlarmViewModel(get(), get()) }
    viewModel { NewAlarmViewModel() }
    viewModel {
        TimerViewModel(
        )
    }
    viewModel {
        StopWatchViewModel(
        )
    }

    viewModel {
        QuickAlarmViewModel()
    }
}
