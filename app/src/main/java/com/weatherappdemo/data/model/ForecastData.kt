package com.weatherappdemo.data.model

data class ForecastData(
    val city: City,
    val list: List<ForecastItem>
)

data class City(
    val name: String
)

data class ForecastItem(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val dt_txt: String // Forecast date and time
)
