package com.weatherappdemo.data.remote.api

import com.weatherappdemo.data.remote.webResponse.ForecastResponse
import com.weatherappdemo.data.remote.webResponse.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServices {
    @GET("weather?")
    suspend fun getCurrentLocationWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = ApiConstants.WEATHER_API_KEY,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>

    @GET("forecast?")
    suspend fun get5DayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = ApiConstants.WEATHER_API_KEY,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>

    @GET("weather?")
    suspend fun getWeatherForecastByCity(
        @Query("q") q: String,
        @Query("appid") apiKey: String = ApiConstants.WEATHER_API_KEY,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>

}