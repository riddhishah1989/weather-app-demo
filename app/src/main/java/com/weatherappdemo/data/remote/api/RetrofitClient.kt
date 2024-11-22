package com.weatherappdemo.data.remote.api

import com.google.gson.GsonBuilder
import com.weatherappdemo.data.repository.WeatherRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    // Base URL
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"


    private val gson = GsonBuilder().setLenient().create()

    private val apiService: APIServices by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(APIServices::class.java)
    }

    val repository: WeatherRepository by lazy {
        WeatherRepository(apiService)
    }

}



