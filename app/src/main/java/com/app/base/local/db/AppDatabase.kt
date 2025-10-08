package com.app.base.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.base.local.dao.AppDAO
import com.app.base.data.entity.AlarmEntity

@Database(entities = [AlarmEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDAO(): AppDAO
}
