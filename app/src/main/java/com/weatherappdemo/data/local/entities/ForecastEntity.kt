package com.weatherappdemo.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast_table",
    indices = [Index(value = ["latitude", "longitude"])],
    foreignKeys = [ForeignKey(
        entity = WeatherEntity::class,
        parentColumns = ["latitude", "longitude"],
        childColumns = ["latitude", "longitude"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val dateTime: Long,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherDescription: String,
    val feelsLike: Double,
    val pressure: Int,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val visibility: Int


)
