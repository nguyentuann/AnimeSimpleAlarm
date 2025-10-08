package com.app.base.di

import com.app.base.repository.AlarmRepository
import com.app.base.repository.AlarmRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<AlarmRepository> { AlarmRepositoryImpl(get(), get()) }
}
