package com.weatherappdemo.data.model

import java.io.Serializable

data class WeatherDataModel(
    val cityName: String,
    var temperature: Double,
    var feelsLike: Double,
    var tempMin: Double,
    var tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val cloudiness: Int,
    val visibility: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
    val weatherMain: String,
    val description: String,
    val icon: String,
    var latitude: Double,
    var longitude: Double,
    var timezone: Int
) : Serializable