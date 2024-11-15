package com.weatherappdemo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// WeatherEntity.kt
@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    var latitude: Double,
    var longitude: Double,
    val weatherDescription: String,
    val humidity: Int,
    val windSpeed: Double,
    val tempMax: Double,
    val tempMin: Double,
    val feelLike: Double,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val lastUpdated: Long, // timestamp for last update
    var isFavCity: Boolean = false
)



