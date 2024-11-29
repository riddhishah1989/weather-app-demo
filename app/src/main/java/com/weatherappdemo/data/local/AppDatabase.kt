package com.weatherappdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weatherappdemo.data.local.dao.WeatherDao
import com.weatherappdemo.data.local.entities.ForecastEntity
import com.weatherappdemo.data.local.entities.WeatherEntity

@Database(
    entities = [WeatherEntity::class, ForecastEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
