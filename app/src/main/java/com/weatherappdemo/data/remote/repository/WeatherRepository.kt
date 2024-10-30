package com.weatherappdemo.data.remote.repository

import com.weatherappdemo.MyApplication
import com.weatherappdemo.R
import com.weatherappdemo.data.remote.api.APIResponse
import com.weatherappdemo.data.remote.api.APIServices
import com.weatherappdemo.data.remote.webResponse.ForecastResponse
import com.weatherappdemo.data.remote.webResponse.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private val apiServices: APIServices) {

    private val application = MyApplication.instance

    suspend fun getCurrentWeather(lat: Double, long: Double): APIResponse<WeatherResponse> =
        withContext(Dispatchers.IO) {
            try {
                // Fetch weather data from repository in IO context
                val response = apiServices.getCurrentLocationWeather(lat, long)

                // Handle the response
                if (response.isSuccessful) {
                    response.body()?.let {
                        APIResponse.Success(it) // Directly return Success
                    }
                        ?: APIResponse.Error(application.getString(R.string.no_data_available)) // Handle null body
                } else {
                    APIResponse.Error(application.getString(R.string.failed_to_fetch_weather))
                }
            } catch (e: Exception) {
                APIResponse.Error(application.getString(R.string.network_error, e.message))
            }
        }


    suspend fun getWeatherForecast(cityName: String): APIResponse<ForecastResponse> =
        withContext(Dispatchers.IO)
        {
            try {
                val response = apiServices.getWeatherForecast(cityName)

                // Handle the response
                if (response.isSuccessful) {
                    response.body()?.let {
                        APIResponse.Success(it)
                    }
                        ?: APIResponse.Error(application.getString(R.string.no_data_available)) // Handle null body
                } else {
                    APIResponse.Error(application.getString(R.string.failed_to_fetch_weather))
                }
            } catch (e: Exception) {
                APIResponse.Error(application.getString(R.string.network_error, e.message))
            }
        }

}
