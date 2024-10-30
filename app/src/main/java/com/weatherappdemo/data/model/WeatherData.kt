package com.weatherappdemo.data.model

data class WeatherData(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val description: String
)

data class Wind(
    val speed: Double
)
