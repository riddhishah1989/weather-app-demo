package com.weatherappdemo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weatherappdemo.data.model.Main
import com.weatherappdemo.data.model.Weather
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.model.Wind

// WeatherEntity.kt
@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    val weatherDescription: String,
    val humidity: Int,
    val windSpeed: Double,
    val lastUpdated: Long, // timestamp for last update
    var isFavCity: Boolean = false
)



