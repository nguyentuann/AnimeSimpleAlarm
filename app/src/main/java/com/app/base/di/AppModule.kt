package com.app.base.di

import androidx.room.Room
import com.app.base.local.db.AppDatabase
import com.app.base.local.db.AppPreferences
import com.app.base.ui.alarm.AlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    // DAO
    single { get<AppDatabase>().appDAO() }

    // Shared Preferences
    single { AppPreferences(androidContext()) }
}

val alarmModule = module {
    single { AlarmScheduler(get()) }
}

