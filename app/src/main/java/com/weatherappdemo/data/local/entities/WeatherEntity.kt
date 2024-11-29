package com.weatherappdemo.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_table",
    indices = [Index(value = ["latitude", "longitude"], unique = true)]
)
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val cityName: String,
    val country: String,
    val temperature: Double,
    var latitude: Double,
    var longitude: Double,
    val weatherMain: String,
    val weatherDescription: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val tempMax: Double,
    val tempMin: Double,
    val feelLike: Double,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val pressure: Int,
    val visibility: Int,
    val timezone: Int,
    val lastUpdated: Long, // timestamp for last update
    var isCurrentLocation: Boolean
)




