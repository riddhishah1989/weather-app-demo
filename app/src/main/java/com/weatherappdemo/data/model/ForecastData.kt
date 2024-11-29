package com.weatherappdemo.data.model

data class ForecastData(
    val dateTime: String,
    val temperature: Double,
    val feelsLike: Double,
    var minTemperature: Double,
    var maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val icon: String,
    val windSpeed: Double,
    val windDirection: Int,
    val cloudiness: Int,
    val visibility: Int,
    val probabilityOfPrecipitation: Double,
    val timezone: Int
)