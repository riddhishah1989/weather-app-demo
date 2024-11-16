package com.weatherappdemo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weatherappdemo.data.local.entities.WeatherEntity

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeatherData(weather: WeatherEntity)

    @Query("SELECT * FROM weather_table")
    suspend fun getAllWeatherData(): List<WeatherEntity>

    @Delete
    suspend fun deleteWeatherData(weather: WeatherEntity)
}

